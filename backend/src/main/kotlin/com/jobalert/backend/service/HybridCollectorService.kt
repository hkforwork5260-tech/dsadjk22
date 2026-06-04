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
 * 책임: fetch → 소스 간 externalId 기준 dedup → 집계.
 * TODO Phase 3: 회사명 정규화·매칭, 어제 대비 diff(NEW/UPDATE/CLOSING/EXPIRED), DB upsert, 푸시 큐 적재.
 */
@Service
class HybridCollectorService(
    private val sources: List<JobSource>,
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

        log.info(
            "hybrid collection end. perSource={} total={} deduped={} (Phase 3에서 diff·DB 적재 예정)",
            perSource, all.size, deduped.size,
        )
        return CollectionResult(
            perSourceCounts = perSource.toMap(),
            totalFetched = all.size,
            dedupedCount = deduped.size,
        )
    }

    data class CollectionResult(
        val perSourceCounts: Map<String, Int>,
        val totalFetched: Int,
        val dedupedCount: Int,
    )
}
