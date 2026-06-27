package com.jobalert.backend.client.source.recruiter

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.RecruiterTenant
import com.jobalert.backend.client.source.SourceRegistry
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
 * recruiter.co.kr(MIDAS jobflex) 공개 API 소스.
 *
 * 한국 대기업이 가장 많이 쓰는 공유 ATS. 한 어댑터가 `prefix` 헤더만 바꿔 다사 커버
 * (KT·현대모비스·HL그룹·DB그룹·SPC·NH투자증권·웹젠·콜마 등 [SourceRegistry.recruiterTenants]).
 * 인증 불필요. `POST /position/v1/jobflex` 페이지네이션(totalPages).
 *
 * 신형 jobflex가 아닌 테넌트는 400 `NotFoundPostedDesignException`을 주므로 그 테넌트만 스킵.
 * best-effort: 테넌트 하나가 실패해도 나머지는 계속(개별 try/catch).
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.recruiter.enabled"], havingValue = "true", matchIfMissing = true)
class RecruiterSource(
    private val apiCallLogger: ApiCallLogger,
    private val registry: SourceRegistry,
    @Value("\${jobalert.sources.recruiter.base-url:https://api-recruiter.recruiter.co.kr}") private val baseUrl: String,
    @Value("\${jobalert.sources.recruiter.page-size:100}") private val pageSize: Int,
    @Value("\${jobalert.sources.recruiter.max-pages:20}") private val maxPages: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "recruiter"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val tenants = registry.recruiterTenants
        log.info("recruiter.fetchAll tenants={}", tenants.size)
        return tenants.flatMap { fetchTenant(it) }
    }

    private fun fetchTenant(tenant: RecruiterTenant): List<RawJobPosting> {
        val out = mutableListOf<RawJobPosting>()
        var page = 1
        val started = System.currentTimeMillis()
        try {
            while (page <= maxPages) {
                val body = """{"pageableRq":{"page":$page,"size":$pageSize,"sort":["CREATED_DATE_TIME"]},"filter":{}}"""
                val resp = restClient.post()
                    .uri("$baseUrl/position/v1/jobflex")
                    .header("prefix", "${tenant.tenant}.recruiter.co.kr")
                    .body(body)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError) { _, res ->
                        throw RestClientException("recruiter ${tenant.tenant} status=${res.statusCode.value()}")
                    }
                    .body(RecruiterJobflexResponse::class.java)

                val items = resp?.list ?: emptyList()
                if (items.isEmpty()) break
                out += items.mapNotNull { RecruiterMapper.toRawJob(tenant, it) }

                val totalPages = resp?.pagination?.totalPages ?: 1
                if (page >= totalPages) break
                page++
            }
            logCall(tenant, 200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("recruiter tenant={} collected={}", tenant.tenant, out.size)
        } catch (ex: RestClientException) {
            // 구버전/career 테넌트는 400(NotFoundPostedDesign) → 스킵. 그 외 네트워크 오류도 흡수.
            logCall(tenant, null, (System.currentTimeMillis() - started).toInt(), out.size, ex.message)
            log.warn("recruiter tenant={} 실패/스킵(부분 {}건): {}", tenant.tenant, out.size, ex.message)
        }
        return out
    }

    private fun logCall(
        tenant: RecruiterTenant,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int,
        errorMessage: String? = null,
    ) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "jobflex",
            params = mapOf("tenant" to tenant.tenant, "result_count" to resultCount),
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
