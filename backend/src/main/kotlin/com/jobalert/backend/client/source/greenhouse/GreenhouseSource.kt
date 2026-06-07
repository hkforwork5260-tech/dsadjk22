package com.jobalert.backend.client.source.greenhouse

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceBoard
import com.jobalert.backend.client.source.SourceRegistry
import com.jobalert.backend.client.source.SourceUtil
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
 * Greenhouse Job Board 공개 API 소스.
 *
 * 인증 불필요. 보드 토큰마다 GET /v1/boards/{token}/jobs?content=false 호출.
 * 글로벌 빅테크 한국지사 커버(한국 근무지 공고만 필터).
 *
 * best-effort: 보드 하나가 실패해도 나머지는 계속 (개별 try/catch).
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.greenhouse.enabled"], havingValue = "true", matchIfMissing = true)
class GreenhouseSource(
    private val apiCallLogger: ApiCallLogger,
    private val registry: SourceRegistry,
    @Value("\${jobalert.sources.greenhouse.base-url:https://boards-api.greenhouse.io/v1/boards}") private val baseUrl: String,
    // content=true면 공고 본문(HTML)까지 받아 응답이 수십 배 커진다. 메모리 작은 클라우드에선
    // 큰 응답 처리가 매우 느려질 수 있어 기본 false(목록만 가볍게). 본문 보강은 별도.
    @Value("\${jobalert.sources.greenhouse.content:false}") private val fetchContent: Boolean,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "greenhouse"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val boards = registry.greenhouseBoards
        log.info("greenhouse.fetchAll boards={}", boards.size)
        return boards.flatMap { fetchBoard(it) }
    }

    private fun fetchBoard(board: SourceBoard): List<RawJobPosting> {
        // content=true면 본문(content)+부서까지. 응답이 커서 작은 클라우드 박스에선 느림 → 기본 false.
        val url = "$baseUrl/${board.token}/jobs?content=$fetchContent"
        val started = System.currentTimeMillis()
        return try {
            val response = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("greenhouse ${board.token} status=${res.statusCode.value()}")
                }
                .body(GreenhouseJobsResponse::class.java)

            val jobs = response?.jobs ?: emptyList()
            val mapped = jobs.mapNotNull { toRawJob(board, it) }
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(board, statusCode = 200, durationMs = duration, resultCount = mapped.size)
            log.info("greenhouse board={} fetched={} kept(korea={})={}", board.token, jobs.size, board.koreaOnly, mapped.size)
            mapped
        } catch (ex: RestClientException) {
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(board, statusCode = null, durationMs = duration, errorMessage = ex.message)
            log.warn("greenhouse board={} 실패(스킵): {}", board.token, ex.message)
            emptyList()
        }
    }

    private fun toRawJob(board: SourceBoard, job: GreenhouseJob): RawJobPosting? {
        val id = job.id ?: return null
        val title = job.title?.takeIf { it.isNotBlank() } ?: return null
        val location = job.location?.name

        if (board.koreaOnly && !SourceUtil.isKoreaLocation(location)) return null

        return RawJobPosting(
            source = sourceId,
            externalId = "greenhouse-${board.token}-$id",
            title = title,
            companyName = job.company_name?.takeIf { it.isNotBlank() } ?: board.displayName,
            companyHomepage = board.homepage,
            location = location,
            department = job.departments.firstOrNull()?.name?.takeIf { it.isNotBlank() },
            postingDateEpoch = SourceUtil.isoToEpochSeconds(job.first_published ?: job.updated_at),
            deadlineEpoch = SourceUtil.isoToEpochSeconds(job.application_deadline),
            originalUrl = job.absolute_url,
            description = SourceUtil.htmlToText(job.content),
        )
    }

    private fun logCall(
        board: SourceBoard,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int? = null,
        errorMessage: String? = null,
    ) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "jobs",
            params = buildMap<String, Any?> {
                put("board", board.token)
                resultCount?.let { put("result_count", it) }
            },
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
