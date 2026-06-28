package com.jobalert.backend.client.source.lx

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.service.ApiCallLogger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestFactory
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import java.time.Duration

/**
 * LX 그룹 통합 채용(apply.lxcareers.com) 소스.
 *
 * `POST /app/job/RetrieveJobNoticesList.rpi` 1콜로 LX 계열사(하우시스·세미콘·인터내셔널·판토스)
 * 진행중 공고를 받는다(HTML 조각). 인증 없음. 파싱은 [LxListParser](Jsoup).
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.lx.enabled"], havingValue = "true", matchIfMissing = true)
class LxSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.lx.base-url:https://apply.lxcareers.com}") private val baseUrl: String,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "lx"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val started = System.currentTimeMillis()
        return try {
            val html = restClient.post()
                .uri("$baseUrl/app/job/RetrieveJobNoticesList.rpi")
                .body("{}")
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("lx status=${res.statusCode.value()}")
                }
                .body(String::class.java)
            val out = html?.let { LxListParser.parse(it) } ?: emptyList()
            logCall(200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("lx collected={}", out.size)
            out
        } catch (ex: RestClientException) {
            logCall(null, (System.currentTimeMillis() - started).toInt(), 0, ex.message)
            log.warn("lx 수집 실패(스킵): {}", ex.message)
            emptyList()
        }
    }

    private fun logCall(statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "RetrieveJobNoticesList",
            params = mapOf("result_count" to resultCount),
            statusCode = statusCode,
            durationMs = durationMs,
            errorMessage = errorMessage,
        )
    }

    private fun timeoutRequestFactory(): ClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(Duration.ofSeconds(3))
            setReadTimeout(Duration.ofSeconds(10))
        }

    companion object {
        private const val USER_AGENT = "JobAlert/0.1 (job-alert app; contact: lhgdlagusurd@naver.com)"
    }
}
