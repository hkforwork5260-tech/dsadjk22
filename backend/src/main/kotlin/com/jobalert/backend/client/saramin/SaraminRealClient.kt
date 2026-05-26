package com.jobalert.backend.client.saramin

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Phase 3에서 실제 사람인 OpenAPI 호출 구현 예정.
 * 지금은 stub — 'mode=real' 활성화 시 명시적으로 throw하여 Phase 3 작업이 시작됐음을 알린다.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.saramin.mode"], havingValue = "real")
class SaraminRealClient(
    @Value("\${jobalert.saramin.api-key:}") private val apiKey: String,
    @Value("\${jobalert.saramin.base-url}") private val baseUrl: String,
) : SaraminClient {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun fetchJobs(params: SaraminFetchParams): List<SaraminJobDto> {
        require(apiKey.isNotBlank()) { "jobalert.saramin.api-key 미설정" }
        log.warn("SaraminRealClient.fetchJobs는 Phase 3에서 구현됩니다. baseUrl={} params={}", baseUrl, params)
        // TODO Phase 3: RestClient.builder().baseUrl(baseUrl) ... GET ?access-key=... 호출
        return emptyList()
    }
}
