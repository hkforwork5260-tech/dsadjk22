package com.jobalert.backend.client.saramin

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneOffset

@Component
@ConditionalOnProperty(name = ["jobalert.saramin.mode"], havingValue = "mock", matchIfMissing = true)
class SaraminMockClient : SaraminClient {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun fetchJobs(params: SaraminFetchParams): List<SaraminJobDto> {
        log.info("[mock] saramin.fetchJobs params={}", params)
        val base = LocalDate.of(2026, 5, 26)
        return listOf(
            sample("saramin-46123456", "삼성전자", "2026 상반기 신입공채", "수원", base.plusDays(20)),
            sample("saramin-46123457", "네이버", "신입 백엔드 개발자", "판교", base.plusDays(15)),
            sample("saramin-46123458", "LG에너지솔루션", "연구개발(R&D) 신입", "대전", base.plusDays(12)),
            sample("saramin-46123459", "카카오", "신입 안드로이드 개발자", "판교", base.plusDays(10)),
            sample("saramin-46123460", "포스코", "2026 상반기 신입공채", "포항", base.plusDays(8)),
        )
    }

    private fun sample(id: String, company: String, title: String, location: String, deadline: LocalDate) =
        SaraminJobDto(
            externalId = id,
            title = title,
            companyName = company,
            location = location,
            experience = "신입",
            education = "학사+",
            postingDateEpoch = LocalDate.of(2026, 5, 25).atStartOfDay(ZoneOffset.UTC).toEpochSecond(),
            deadlineEpoch = deadline.atStartOfDay(ZoneOffset.UTC).toEpochSecond(),
            originalUrl = "https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=$id",
        )
}
