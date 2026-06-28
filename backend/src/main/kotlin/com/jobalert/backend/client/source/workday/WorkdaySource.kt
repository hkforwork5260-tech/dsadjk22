package com.jobalert.backend.client.source.workday

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceRegistry
import com.jobalert.backend.client.source.WorkdayTenant
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
 * Workday(CXS) 공개 채용 API 소스.
 *
 * 한 어댑터가 테넌트만 바꿔 다사 커버([SourceRegistry.workdayTenants], 현재 대웅제약).
 * `POST /wday/cxs/{cxsTenant}/{site}/jobs` offset 페이지네이션. 인증 없음.
 * best-effort: 테넌트 하나가 실패해도 나머지는 계속.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.workday.enabled"], havingValue = "true", matchIfMissing = true)
class WorkdaySource(
    private val apiCallLogger: ApiCallLogger,
    private val registry: SourceRegistry,
    @Value("\${jobalert.sources.workday.page-size:20}") private val pageSize: Int,
    @Value("\${jobalert.sources.workday.max-pages:25}") private val maxPages: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "workday"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val tenants = registry.workdayTenants
        log.info("workday.fetchAll tenants={}", tenants.size)
        return tenants.flatMap { fetchTenant(it) }
    }

    private fun fetchTenant(t: WorkdayTenant): List<RawJobPosting> {
        val out = mutableListOf<RawJobPosting>()
        var offset = 0
        var page = 0
        val started = System.currentTimeMillis()
        try {
            while (page < maxPages) {
                val body = """{"limit":$pageSize,"offset":$offset,"searchText":""}"""
                val resp = restClient.post()
                    .uri("https://${t.host}.myworkdayjobs.com/wday/cxs/${t.cxsTenant}/${t.site}/jobs")
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError) { _, res ->
                        throw RestClientException("workday ${t.cxsTenant} status=${res.statusCode.value()}")
                    }
                    .body(WorkdayResponse::class.java)

                val items = resp?.jobPostings ?: emptyList()
                if (items.isEmpty()) break
                out += items.mapNotNull { WorkdayMapper.toRawJob(t, it) }

                offset += pageSize
                page++
                if (offset >= (resp?.total ?: 0)) break
            }
            logCall(t, 200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("workday tenant={} collected={}", t.cxsTenant, out.size)
        } catch (ex: RestClientException) {
            logCall(t, null, (System.currentTimeMillis() - started).toInt(), out.size, ex.message)
            log.warn("workday tenant={} 실패/스킵(부분 {}건): {}", t.cxsTenant, out.size, ex.message)
        }
        return out
    }

    private fun logCall(t: WorkdayTenant, statusCode: Int?, durationMs: Int, resultCount: Int, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "cxs/jobs",
            params = mapOf("tenant" to t.cxsTenant, "result_count" to resultCount),
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
