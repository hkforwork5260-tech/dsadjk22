package com.jobalert.backend.client.source.cj

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
 * CJ 그룹 통합 채용포털 소스.
 *
 * `POST recruit.cj.net/.../searchNewGonggoList.fo` form POST 1콜로 CJ그룹 전체 계열사
 * (제일제당·ENM·대한통운·올리브영 등) 통합 수집. 인증 없음. compnm이 계열사명으로 직접 옴.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.cj.enabled"], havingValue = "true", matchIfMissing = true)
class CjSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.cj.base-url:https://recruit.cj.net}") private val baseUrl: String,
    @Value("\${jobalert.sources.cj.page-size:2000}") private val pageSize: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "cj"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .defaultHeader("X-Requested-With", "XMLHttpRequest")
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val started = System.currentTimeMillis()
        return try {
            val resp = restClient.post()
                .uri("$baseUrl/recruit/ko/recruit/recruit/searchNewGonggoList.fo")
                .body("pageIndex=1&pageSize=$pageSize")
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("cj status=${res.statusCode.value()}")
                }
                .body(CjResponse::class.java)
            val out = (resp?.list ?: emptyList()).mapNotNull { CjMapper.toRawJob(it) }
            logCall(200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("cj collected={}", out.size)
            out
        } catch (ex: RestClientException) {
            logCall(null, (System.currentTimeMillis() - started).toInt(), 0, ex.message)
            log.warn("cj 수집 실패(스킵): {}", ex.message)
            emptyList()
        }
    }

    private fun logCall(statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "searchNewGonggoList",
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
