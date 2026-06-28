package com.jobalert.backend.client.source.lg

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
 * LG 통합 채용 API 소스.
 *
 * `POST api.careers.lg.com/rmk/job/retrieveJobNoticesList` 빈 필터 1콜로 LG그룹 전체 계열사
 * (전자·화학·CNS·에너지솔루션·이노텍·디스플레이·유플러스·생활건강·Magna 등) 통합 수집. 인증 없음.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.lg.enabled"], havingValue = "true", matchIfMissing = true)
class LgSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.lg.base-url:https://api.careers.lg.com}") private val baseUrl: String,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "lg"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val started = System.currentTimeMillis()
        return try {
            val resp = restClient.post()
                .uri("$baseUrl/rmk/job/retrieveJobNoticesList")
                .body(REQUEST_BODY)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("lg status=${res.statusCode.value()}")
                }
                .body(LgResponse::class.java)
            val out = (resp?.data?.jobNoticeList ?: emptyList()).mapNotNull { LgMapper.toRawJob(it) }
            logCall(200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("lg collected={}", out.size)
            out
        } catch (ex: RestClientException) {
            logCall(null, (System.currentTimeMillis() - started).toInt(), 0, ex.message)
            log.warn("lg 수집 실패(스킵): {}", ex.message)
            emptyList()
        }
    }

    private fun logCall(statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "retrieveJobNoticesList",
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

        // 빈 필터 = 전체 진행중 공고. recDate desc 정렬.
        private const val REQUEST_BODY =
            """{"lnbSearch":"","hashTagText":"","recDate":"CREATION_DATE","order":"DESC","careerList":[],"companyCodeList":[],"desireLocList":[],"jobGroupList":[]}"""
    }
}
