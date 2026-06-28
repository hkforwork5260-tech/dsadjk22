package com.jobalert.backend.client.source.naver

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
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
 * 네이버 채용 공개 API 소스.
 *
 * `GET recruit.navercorp.com/rcrt/loadJobList.do?recordCountPerPage={n}` 한 번에 NAVER+계열사
 * (네이버웹툰 등) 진행중 공고 전체를 받는다. 인증 없음. recordCountPerPage를 크게 줘 단일 호출.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.naver.enabled"], havingValue = "true", matchIfMissing = true)
class NaverSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.naver.base-url:https://recruit.navercorp.com}") private val baseUrl: String,
    @Value("\${jobalert.sources.naver.max-rows:500}") private val maxRows: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    // 네이버 JSON 키는 camelCase. 전역 SNAKE_CASE 매퍼와 안 맞아 전용 평매퍼로 파싱.
    private val objectMapper = jacksonObjectMapper()

    override val sourceId = "naver"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val started = System.currentTimeMillis()
        return try {
            // 네이버는 JSON 본문을 Content-Type text/html로 보내 Jackson 컨버터가 안 먹음 → String으로 받아 직접 파싱.
            val rawBody = restClient.get()
                .uri("$baseUrl/rcrt/loadJobList.do?firstIndex=0&recordCountPerPage=$maxRows")
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("naver status=${res.statusCode.value()}")
                }
                .body(String::class.java)
            val resp = rawBody?.let { objectMapper.readValue(it, NaverResponse::class.java) }
            val out = (resp?.list ?: emptyList()).mapNotNull { NaverMapper.toRawJob(it) }
            logCall(200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("naver collected={}", out.size)
            out
        } catch (ex: RestClientException) {
            logCall(null, (System.currentTimeMillis() - started).toInt(), 0, ex.message)
            log.warn("naver 수집 실패(스킵): {}", ex.message)
            emptyList()
        }
    }

    private fun logCall(statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "loadJobList",
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
