package com.jobalert.backend.client.source.toss

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
 * 토스(비바리퍼블리카) 공개 채용 API 소스.
 *
 * `GET api-public.toss.im/api/v3/ipd-eggnog/career/job-groups` 한 번에 그룹사 전체(토스·토스뱅크·
 * 토스증권·토스페이먼츠 등) 통합 공고를 받는다. 인증·페이지네이션 없음(단일 호출).
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.toss.enabled"], havingValue = "true", matchIfMissing = true)
class TossSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.toss.base-url:https://api-public.toss.im}") private val baseUrl: String,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "toss"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val started = System.currentTimeMillis()
        return try {
            val resp = restClient.get()
                .uri("$baseUrl/api/v3/ipd-eggnog/career/job-groups")
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("toss status=${res.statusCode.value()}")
                }
                .body(TossResponse::class.java)
            val out = (resp?.success ?: emptyList()).mapNotNull { TossMapper.toRawJob(it) }
            logCall(200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("toss collected={}", out.size)
            out
        } catch (ex: RestClientException) {
            logCall(null, (System.currentTimeMillis() - started).toInt(), 0, ex.message)
            log.warn("toss 수집 실패(스킵): {}", ex.message)
            emptyList()
        }
    }

    private fun logCall(statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "job-groups",
            params = mapOf("result_count" to resultCount),
            statusCode = statusCode,
            durationMs = durationMs,
            errorMessage = errorMessage,
        )
    }

    private fun timeoutRequestFactory(): ClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(Duration.ofSeconds(3))
            setReadTimeout(Duration.ofSeconds(15))
        }

    companion object {
        private const val USER_AGENT = "JobAlert/0.1 (job-alert app; contact: lhgdlagusurd@naver.com)"
    }
}
