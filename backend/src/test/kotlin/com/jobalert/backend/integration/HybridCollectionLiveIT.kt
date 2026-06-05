package com.jobalert.backend.integration

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.service.HybridCollectorService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertTrue

/**
 * 라이브 통합 테스트 — 전체 Spring 컨텍스트를 부팅하고 실제 외부 API에서 공고를 수집한다.
 *
 * 일반 `./gradlew test`에선 건너뜀(JOBALERT_PUBINST_KEY 환경변수 있을 때만 실행).
 * 실행: docker(postgres+redis) 띄운 뒤
 *   JOBALERT_PUBINST_KEY=<키> ./gradlew test --tests "*HybridCollectionLiveIT"
 *
 * 검증 대상: Spring 빈 배선 + HybridCollectorService가 Greenhouse·공공기관 소스에서
 *           실제 한국 공고를 긁어오는지(end-to-end).
 */
@SpringBootTest(properties = ["jobalert.sources.public-institution.enabled=true"])
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "JOBALERT_PUBINST_KEY", matches = ".+")
class HybridCollectionLiveIT {

    @Autowired
    lateinit var collector: HybridCollectorService

    @Autowired
    lateinit var sources: List<JobSource>

    @Test
    fun `실제 외부 API에서 공고를 수집한다`() {
        println("\n=== 활성 소스: ${sources.map { it.sourceId }} ===")

        val result = collector.runDailyCollection()

        println("\n=== 수집 결과 ===")
        result.perSourceCounts.forEach { (src, n) -> println("  $src : ${n}건") }
        println("  합계 ${result.totalFetched}건 (dedup 후 ${result.dedupedCount}건)")

        // 소스별 실제 공고 샘플 2건 출력 (눈으로 확인)
        println("\n=== 실제 공고 샘플 ===")
        sources.forEach { source ->
            source.fetchAll().take(2).forEach { job ->
                println("  [${job.source}] ${job.title}")
                println("      회사=${job.companyName} 지역=${job.location} 마감(epoch)=${job.deadlineEpoch}")
                println("      링크=${job.originalUrl}")
            }
        }

        assertTrue(result.totalFetched > 0, "공고가 한 건도 안 들어왔다 — 소스 확인 필요")
    }
}
