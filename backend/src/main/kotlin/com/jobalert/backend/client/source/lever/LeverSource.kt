package com.jobalert.backend.client.source.lever

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
 * Lever Postings 공개 API 소스.
 *
 * 인증 불필요. company slug마다 GET /v0/postings/{slug}?mode=json 호출.
 * 응답은 JSON 배열. 한국 근무지/국가 공고만 필터(koreaOnly).
 *
 * best-effort: 회사 하나가 실패해도 나머지는 계속.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.lever.enabled"], havingValue = "true", matchIfMissing = true)
class LeverSource(
    private val apiCallLogger: ApiCallLogger,
    private val registry: SourceRegistry,
    @Value("\${jobalert.sources.lever.base-url:https://api.lever.co/v0/postings}") private val baseUrl: String,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "lever"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val boards = registry.leverBoards
        log.info("lever.fetchAll companies={}", boards.size)
        return boards.flatMap { fetchCompany(it) }
    }

    private fun fetchCompany(board: SourceBoard): List<RawJobPosting> {
        val url = "$baseUrl/${board.token}?mode=json"
        val started = System.currentTimeMillis()
        return try {
            val response = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("lever ${board.token} status=${res.statusCode.value()}")
                }
                .body(Array<LeverPosting>::class.java)

            val postings = response?.toList() ?: emptyList()
            val mapped = postings.mapNotNull { toRawJob(board, it) }
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(board, statusCode = 200, durationMs = duration, resultCount = mapped.size)
            log.info("lever company={} fetched={} kept(korea={})={}", board.token, postings.size, board.koreaOnly, mapped.size)
            mapped
        } catch (ex: RestClientException) {
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(board, statusCode = null, durationMs = duration, errorMessage = ex.message)
            log.warn("lever company={} 실패(스킵): {}", board.token, ex.message)
            emptyList()
        }
    }

    private fun toRawJob(board: SourceBoard, posting: LeverPosting): RawJobPosting? {
        val id = posting.id?.takeIf { it.isNotBlank() } ?: return null
        val title = posting.text?.takeIf { it.isNotBlank() } ?: return null
        val location = posting.categories?.location

        if (board.koreaOnly && !isKorea(location, posting.country)) return null

        return RawJobPosting(
            source = sourceId,
            externalId = "lever-${board.token}-$id",
            title = title,
            companyName = board.displayName,
            companyHomepage = board.homepage,
            location = location,
            department = posting.categories?.department ?: posting.categories?.team,
            postingDateEpoch = SourceUtil.millisToEpochSeconds(posting.createdAt),
            deadlineEpoch = null, // Lever는 마감 개념 없음
            originalUrl = posting.hostedUrl ?: posting.applyUrl,
        )
    }

    /** Lever는 country 코드("KR")도 주므로 location 텍스트 + country 둘 다로 한국 판정. */
    private fun isKorea(location: String?, country: String?): Boolean =
        country.equals("KR", ignoreCase = true) || SourceUtil.isKoreaLocation(location)

    private fun logCall(
        board: SourceBoard,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int? = null,
        errorMessage: String? = null,
    ) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "postings",
            params = buildMap<String, Any?> {
                put("company", board.token)
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
