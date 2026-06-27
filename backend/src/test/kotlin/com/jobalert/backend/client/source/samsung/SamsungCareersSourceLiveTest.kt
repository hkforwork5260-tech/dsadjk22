package com.jobalert.backend.client.source.samsung

import com.jobalert.backend.service.ApiCallLogger
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.assertTrue

/**
 * 라이브 테스트 — 실제 samsungcareers.com `list.data`를 호출해 RestClient 폼 POST·헤더가
 * 살아있는지 end-to-end 검증. Spring 부팅 없이 소스만 직접 띄운다(빠름).
 *
 * 일반 `./gradlew test`에선 건너뜀. 실행:
 *   SAMSUNG_LIVE=1 ./gradlew test --tests "*SamsungCareersSourceLiveTest"
 */
@EnabledIfEnvironmentVariable(named = "SAMSUNG_LIVE", matches = ".+")
class SamsungCareersSourceLiveTest {

    @Test
    fun `실제 삼성 채용에서 공고를 수집한다`() {
        val logger = mockk<ApiCallLogger>(relaxed = true)
        every { logger.log(any(), any(), any(), any(), any(), any()) } returns Unit

        val source = SamsungCareersSource(
            apiCallLogger = logger,
            baseUrl = "https://www.samsungcareers.com/hr",
            maxPages = 3,
        )

        val jobs = source.fetchAll()
        println("\n=== 삼성 수집 ${jobs.size}건 ===")
        jobs.take(10).forEach { println("  [${it.companyName}] ${it.title} (~${it.deadlineEpoch}) ${it.originalUrl}") }

        assertTrue(jobs.isNotEmpty(), "공고가 0건 — 엔드포인트/파서 점검 필요")
        assertTrue(jobs.all { it.externalId.startsWith("samsung-") })
        assertTrue(jobs.all { it.originalUrl?.contains("/hr/?no=") == true })
    }
}
