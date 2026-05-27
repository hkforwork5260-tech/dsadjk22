package com.jobalert.backend.service

import com.jobalert.backend.client.saramin.SaraminApiException
import com.jobalert.backend.client.saramin.SaraminClient
import com.jobalert.backend.client.saramin.SaraminFetchParams
import com.jobalert.backend.client.saramin.SaraminJobDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * 매일 사람인 공고 수집·정규화·diff 처리.
 *
 * v0.1 수집 전략 (A안):
 *  - publishedMin=어제 00:00 KST → "어제 이후 등록·갱신된 공고"만 fetch
 *  - sr=directhire → 헤드헌팅·파견업체 제외
 *  - sort=pd (등록일↓), start=0부터 페이지네이션 (110개씩)
 *  - 회사 매칭은 백엔드에서 (사람인 API는 회사 규모 필터 미지원)
 *
 * Phase 1: 호출만 하고 결과 로그. DB 적재(dedup·정규화)는 Phase 3.
 */
@Service
class JobCollectorService(
    private val saramin: SaraminClient,
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.saramin.daily-call-limit:500}") private val dailyCallLimit: Int,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun runDailyCollection(): CollectionResult {
        val callsBefore = apiCallLogger.countSaraminCallsLast24h()
        log.info("daily collection start. saramin calls last 24h = {} / {}", callsBefore, dailyCallLimit)

        val publishedMin = LocalDate.now(ZONE_KST).minusDays(1)
            .atStartOfDay(ZONE_KST)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val all = mutableListOf<SaraminJobDto>()
        var page = 0
        var totalCalls = 0

        while (page < MAX_PAGES) {
            if (callsBefore + totalCalls >= dailyCallLimit) {
                log.warn("daily call limit({}) 도달. 수집 중단. fetched so far={}", dailyCallLimit, all.size)
                break
            }

            val params = SaraminFetchParams(
                publishedMin = publishedMin,
                sr = "directhire",
                sort = "pd",
                start = page,
                count = PAGE_SIZE,
            )

            val batch = fetchWithRetry(params) ?: break
            totalCalls++
            all += batch

            log.info("page={} fetched={} total_so_far={}", page, batch.size, all.size)
            if (batch.size < PAGE_SIZE) break // 마지막 페이지
            page++
        }

        log.info(
            "daily collection end. pages={} fetched={} (Phase 3에서 dedup·DB 적재 구현 예정)",
            totalCalls, all.size,
        )
        // TODO Phase 3: dedup, normalize, upsert into jobs, mark kind (NEW/UPDATE/CLOSING/EXPIRED)
        return CollectionResult(pagesFetched = totalCalls, jobsFetched = all.size)
    }

    /** 5xx만 1회 재시도. 4xx·사람인 application-level 에러는 즉시 throw. */
    private fun fetchWithRetry(params: SaraminFetchParams): List<SaraminJobDto>? {
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                return saramin.fetchJobs(params)
            } catch (ex: SaraminApiException) {
                when {
                    ex.isRateLimited -> {
                        log.error("사람인 일일 한도 초과 (code=4). 수집 중단.")
                        return null
                    }
                    ex.isAuthError -> {
                        log.error("사람인 access-key 인증 실패. 수집 중단.")
                        return null
                    }
                    ex.isRetryable && attempt < MAX_ATTEMPTS - 1 -> {
                        log.warn("사람인 5xx 재시도 attempt={} status={}", attempt + 1, ex.httpStatus)
                        Thread.sleep(BACKOFF_MS * (attempt + 1))
                    }
                    else -> {
                        log.error("사람인 fetch 최종 실패", ex)
                        return null
                    }
                }
            }
        }
        return null
    }

    data class CollectionResult(val pagesFetched: Int, val jobsFetched: Int)

    companion object {
        private val ZONE_KST = ZoneId.of("Asia/Seoul")
        private const val PAGE_SIZE = 110
        private const val MAX_PAGES = 100 // 안전장치: 한 번 수집에 최대 100페이지 = 11,000건
        private const val MAX_ATTEMPTS = 2
        private const val BACKOFF_MS = 1000L
    }
}
