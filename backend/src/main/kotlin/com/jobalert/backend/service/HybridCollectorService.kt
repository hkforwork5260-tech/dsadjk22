package com.jobalert.backend.service

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 하이브리드 수집기 — 등록된 모든 [JobSource]를 순회하며 공고를 모은다.
 *
 * 사람인 단독 → 하이브리드 전환(2026-06-04). Spring이 모든 JobSource 빈을
 * `List<JobSource>`로 주입한다(Greenhouse·Lever, 향후 그리팅 등). 활성 소스가 없으면 빈 리스트.
 *
 * 책임: fetch → 소스 간 externalId 기준 dedup → [JobPersistenceService]로 적재·diff 위임.
 * TODO Phase 3 잔여: 푸시 큐 적재(적재 결과의 NEW/CLOSING을 FCM 발송 큐로).
 */
@Service
class HybridCollectorService(
    private val sources: List<JobSource>,
    private val persistenceService: JobPersistenceService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 수집 진행 중 플래그 — 수동 트리거 중복 실행(동시 2회 수집) 방지. */
    private val running = AtomicBoolean(false)

    val isRunning: Boolean get() = running.get()

    companion object {
        /** per-source 순차 수집에서 소스 간 대기(ms). 메모리/GC 회복으로 OOM 피크 분산. */
        private const val SOURCE_GAP_MS = 30_000L
    }

    /**
     * 수동 트리거용 비동기 수집. 즉시 반환하고 별도 스레드에서 [runDailyCollection] 실행.
     * 클라우드(Railway) 프록시는 동기 응답이 오래 걸리면 502를 내므로, HTTP는 바로 끊고 작업은 뒤에서 돌린다.
     * 이미 수집 중이면 false 반환(중복 방지). 시작했으면 true.
     */
    @Async
    fun runDailyCollectionAsync(sourceFilter: Set<String>? = null) {
        if (!running.compareAndSet(false, true)) {
            log.warn("이미 수집 진행 중 — 비동기 트리거 무시")
            return
        }
        try {
            runDailyCollection(sourceFilter)
        } catch (ex: Exception) {
            log.error("비동기 수집 실패", ex)
        } finally {
            running.set(false)
        }
    }

    /**
     * 소스를 '하나씩' 순차로 수집한다(각 소스 사이 메모리 회복 대기). 무료 박스 OOM 회피용.
     * 전체를 한 번에 메모리에 올리면(runDailyCollection(null)) OOM으로 박스가 죽으므로,
     * 단독 수집(greenhouse·seoul·pubinst 각각 502 없이 버티는 게 실측됨)을 순서대로 돈다.
     * 한 소스가 실패해도 다음 소스는 계속.
     */
    @Async
    fun runDailyCollectionPerSourceAsync() {
        if (!running.compareAndSet(false, true)) {
            log.warn("이미 수집 진행 중 — per-source 트리거 무시")
            return
        }
        try {
            val ids = sources.map { it.sourceId }
            log.info("per-source 순차 수집 시작: {}", ids)
            for (sid in ids) {
                try {
                    runDailyCollection(setOf(sid))
                } catch (ex: Exception) {
                    log.error("source={} 수집 실패 — 다음 소스 계속", sid, ex)
                }
                Thread.sleep(SOURCE_GAP_MS) // 소스 간 메모리/GC 회복(피크 분산)
            }
            log.info("per-source 순차 수집 완료")
        } catch (ex: Exception) {
            log.error("per-source 수집 실패", ex)
        } finally {
            running.set(false)
        }
    }

    /**
     * @param sourceFilter null이면 전체 소스. 지정하면 해당 sourceId만 수집(예: {"seoul"}).
     *   만료 스윕은 수집한 소스에만 적용되므로(seenBySource 기준) 다른 소스 공고는 건드리지 않는다.
     *   트라이얼 박스 OOM 회피용 — 가벼운 단일 소스만 빠르게 재수집할 때 쓴다.
     */
    /** DB 전체 재분류를 비동기로(소스 재수집 없이). 분류 규칙 개선 후 즉시 반영용. */
    @Async
    fun reclassifyAsync() {
        try {
            persistenceService.reclassifyAll()
        } catch (ex: Exception) {
            log.error("재분류 실패", ex)
        }
    }

    fun runDailyCollection(sourceFilter: Set<String>? = null): CollectionResult {
        val targets = sources.filter { sourceFilter == null || it.sourceId in sourceFilter }
        log.info("hybrid collection start. target sources = {}", targets.map { it.sourceId })

        val perSource = mutableMapOf<String, Int>()
        val all = mutableListOf<RawJobPosting>()

        for (source in targets) {
            val srcStart = System.currentTimeMillis()
            val jobs = try {
                source.fetchAll()
            } catch (ex: Exception) {
                // best-effort: 한 소스가 통째로 터져도 나머지는 계속.
                log.error("source={} 전체 실패(스킵)", source.sourceId, ex)
                emptyList()
            }
            perSource[source.sourceId] = jobs.size
            all += jobs
            log.info("source={} collected={} ({}ms)", source.sourceId, jobs.size, System.currentTimeMillis() - srcStart)
        }

        // 소스 간 중복 제거 (같은 공고가 여러 소스에 잡히는 경우 대비). externalId는 소스 prefix 포함이라
        // 보통 안 겹치지만, 향후 회사명+제목 기반 cross-source dedup은 Phase 3 정규화에서.
        val deduped = all.distinctBy { it.externalId }

        // 메모리까지 가져온 공고를 DB에 적재 + 어제 대비 diff 라벨링.
        log.info("적재 시작: {}건 (DB upsert+diff)", deduped.size)
        val persistStart = System.currentTimeMillis()
        val persist = persistenceService.persist(deduped)
        log.info("적재 완료: {}건 ({}ms)", deduped.size, System.currentTimeMillis() - persistStart)

        log.info(
            "hybrid collection end. perSource={} total={} deduped={} persist={}",
            perSource, all.size, deduped.size, persist,
        )
        return CollectionResult(
            perSourceCounts = perSource.toMap(),
            totalFetched = all.size,
            dedupedCount = deduped.size,
            persist = persist,
        )
    }

    data class CollectionResult(
        val perSourceCounts: Map<String, Int>,
        val totalFetched: Int,
        val dedupedCount: Int,
        val persist: JobPersistenceService.PersistResult? = null,
    )
}
