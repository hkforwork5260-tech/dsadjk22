package com.jobalert.backend.client.source.greeting

import com.jobalert.backend.client.source.GreetingWorkspace
import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
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
 * 그리팅(두들린, greetinghr.com) 공개 채용 API 소스.
 *
 * 무신사·컬리·HYBE·CJ올리브영·현대오토에버 등이 자체 채용페이지 ATS로 사용. 인증 불필요.
 * 한 어댑터가 workspaceId만 바꿔 다사 커버([SourceRegistry.greetingWorkspaces]).
 * `GET /ats/v1.1/career/workspaces/{workspaceId}/openings?page&pageSize` 페이지네이션(hasNext).
 *
 * best-effort: 워크스페이스 하나가 실패해도 나머지는 계속(개별 try/catch).
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.greeting.enabled"], havingValue = "true", matchIfMissing = true)
class GreetingSource(
    private val apiCallLogger: ApiCallLogger,
    private val registry: SourceRegistry,
    @Value("\${jobalert.sources.greeting.base-url:https://api.greetinghr.com}") private val baseUrl: String,
    @Value("\${jobalert.sources.greeting.page-size:100}") private val pageSize: Int,
    @Value("\${jobalert.sources.greeting.max-pages:20}") private val maxPages: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "greeting"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val workspaces = registry.greetingWorkspaces
        log.info("greeting.fetchAll workspaces={}", workspaces.size)
        return workspaces.flatMap { fetchWorkspace(it) }
    }

    private fun fetchWorkspace(ws: GreetingWorkspace): List<RawJobPosting> {
        val out = mutableListOf<RawJobPosting>()
        var page = 0
        val started = System.currentTimeMillis()
        try {
            while (page < maxPages) {
                val resp = restClient.get()
                    .uri("$baseUrl/ats/v1.1/career/workspaces/${ws.workspaceId}/openings?page=$page&pageSize=$pageSize")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError) { _, res ->
                        throw RestClientException("greeting ${ws.subdomain} status=${res.statusCode.value()}")
                    }
                    .body(GreetingResponse::class.java)

                val data = resp?.data
                val items = data?.datas ?: emptyList()
                if (items.isEmpty()) break
                out += items.mapNotNull { GreetingMapper.toRawJob(ws, it) }

                if (data?.hasNext != true) break
                page++
            }
            logCall(ws, 200, (System.currentTimeMillis() - started).toInt(), out.size)
            log.info("greeting workspace={} collected={}", ws.subdomain, out.size)
        } catch (ex: RestClientException) {
            logCall(ws, null, (System.currentTimeMillis() - started).toInt(), out.size, ex.message)
            log.warn("greeting workspace={} 실패/스킵(부분 {}건): {}", ws.subdomain, out.size, ex.message)
        }
        return out
    }

    private fun logCall(
        ws: GreetingWorkspace,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int,
        errorMessage: String? = null,
    ) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "openings",
            params = mapOf("workspace" to ws.subdomain, "result_count" to resultCount),
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
