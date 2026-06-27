package com.jobalert.backend.client.source.recruiter

import com.jobalert.backend.client.source.SourceRegistry
import com.jobalert.backend.service.ApiCallLogger
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertTrue

/**
 * 라이브 — 실제 recruiter jobflex API를 전 tenant 1페이지씩 호출해 배선·매핑이 동작하는지 확인.
 * Spring 부팅 없이 소스만 직접(빠름). 실행:
 *   RECRUITER_LIVE=1 ./gradlew test --tests "*RecruiterSourceLiveTest"
 */
@EnabledIfEnvironmentVariable(named = "RECRUITER_LIVE", matches = ".+")
class RecruiterSourceLiveTest {

    @Test
    fun `실제 recruiter에서 다수 tenant 수집`() {
        val logger = mockk<ApiCallLogger>(relaxed = true)
        every { logger.log(any(), any(), any(), any(), any(), any()) } returns Unit

        // 페이지 1개·5건만 빠르게(배선 검증 목적). 실제 운영은 size 100·전페이지.
        val source = RecruiterSource(
            apiCallLogger = logger,
            registry = SourceRegistry(),
            baseUrl = "https://api-recruiter.recruiter.co.kr",
            pageSize = 5,
            maxPages = 1,
        )

        val jobs = source.fetchAll()
        val byCompany = jobs.groupingBy { it.companyName }.eachCount()
        println("\n=== recruiter 수집 ${jobs.size}건 / 회사 ${byCompany.size}곳 (각 tenant 최대 5건) ===")
        byCompany.entries.sortedByDescending { it.value }.take(20)
            .forEach { (c, n) -> println("  $c: $n") }
        println("샘플:")
        jobs.take(5).forEach { println("  [${it.companyName}] ${it.title} | ~${it.deadlineEpoch} | ${it.originalUrl}") }

        assertTrue(jobs.isNotEmpty(), "0건 — API/매핑 점검 필요")
        assertTrue(jobs.all { it.externalId.startsWith("recruiter-") })
        assertTrue(jobs.all { it.originalUrl?.contains("recruiter.co.kr/career/home?positionSn=") == true })
        // 여러 tenant가 살아있어야 함(최소 5개사 이상 응답 기대).
        assertTrue(byCompany.size >= 5, "응답 회사 ${byCompany.size}곳 — 너무 적음")
    }
}
