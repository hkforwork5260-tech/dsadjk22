package com.jobalert.backend.service

import com.jobalert.backend.client.saramin.SaraminClient
import com.jobalert.backend.client.saramin.SaraminFetchParams
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * 매일 사람인 공고 수집·정규화·diff 처리.
 * Phase 1: 호출만 하고 결과 로그. DB 적재는 Phase 3.
 */
@Service
class JobCollectorService(
    private val saramin: SaraminClient,
    private val apiCallLogger: ApiCallLogger,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun runDailyCollection() {
        val callsToday = apiCallLogger.countSaraminCallsLast24h()
        log.info("daily collection start. saramin calls last 24h = {}", callsToday)

        val started = System.currentTimeMillis()
        val params = SaraminFetchParams(coSize = "large,mid", count = 100)
        val jobs = runCatching { saramin.fetchJobs(params) }
            .onFailure { ex ->
                apiCallLogger.log(
                    source = "saramin",
                    endpoint = "job-search",
                    params = mapOf("co_size" to params.coSize, "count" to params.count),
                    errorMessage = ex.message,
                    durationMs = (System.currentTimeMillis() - started).toInt(),
                )
                log.error("saramin fetch 실패", ex)
            }
            .getOrDefault(emptyList())

        apiCallLogger.log(
            source = "saramin",
            endpoint = "job-search",
            params = mapOf("co_size" to params.coSize, "count" to params.count, "result_count" to jobs.size),
            statusCode = 200,
            durationMs = (System.currentTimeMillis() - started).toInt(),
        )

        log.info("daily collection end. fetched={} (Phase 3에서 dedup·DB 적재 구현 예정)", jobs.size)
        // TODO Phase 3: dedup, normalize, upsert into jobs, mark kind (NEW/UPDATE/CLOSING/EXPIRED)
    }
}
