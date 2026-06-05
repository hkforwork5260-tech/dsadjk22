package com.jobalert.backend.service

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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

    fun runDailyCollection(): CollectionResult {
        log.info("hybrid collection start. active sources = {}", sources.map { it.sourceId })

        val perSource = mutableMapOf<String, Int>()
        val all = mutableListOf<RawJobPosting>()

        for (source in sources) {
            val jobs = try {
                source.fetchAll()
            } catch (ex: Exception) {
                // best-effort: 한 소스가 통째로 터져도 나머지는 계속.
                log.error("source={} 전체 실패(스킵)", source.sourceId, ex)
                emptyList()
            }
            perSource[source.sourceId] = jobs.size
            all += jobs
            log.info("source={} collected={}", source.sourceId, jobs.size)
        }

        // 소스 간 중복 제거 (같은 공고가 여러 소스에 잡히는 경우 대비). externalId는 소스 prefix 포함이라
        // 보통 안 겹치지만, 향후 회사명+제목 기반 cross-source dedup은 Phase 3 정규화에서.
        val deduped = all.distinctBy { it.externalId }

        // 메모리까지 가져온 공고를 DB에 적재 + 어제 대비 diff 라벨링.
        val persist = persistenceService.persist(deduped)

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
