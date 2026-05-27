package com.jobalert.backend.client.saramin

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.time.Duration

/**
 * 사람인 OpenAPI 실 호출 구현.
 *
 * 공식 문서: https://oapi.saramin.co.kr/guide/job-search
 *
 * 책임:
 *  - 단발 GET 요청 (페이지네이션은 호출자가 start 증가시키면서 반복)
 *  - 응답 → 도메인 DTO 매핑
 *  - 사람인 자체 에러 코드 매핑 (1/2/3/4/99 → [SaraminApiException])
 *  - 4xx/5xx HTTP 매핑
 *  - 호출량 로깅 ([ApiCallLogger])
 *
 * 재시도·페이지네이션·일일 한도 체크는 [com.jobalert.backend.service.JobCollectorService] 책임.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.saramin.mode"], havingValue = "real")
class SaraminRealClient(
    @Value("\${jobalert.saramin.api-key:}") private val apiKey: String,
    @Value("\${jobalert.saramin.base-url}") private val baseUrl: String,
    private val objectMapper: ObjectMapper,
    private val apiCallLogger: ApiCallLogger,
) : SaraminClient {

    private val log = LoggerFactory.getLogger(javaClass)

    private val restClient: RestClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", "JobAlert/0.1 (https://github.com/hkforwork5260-tech/dsadjk22)")
        .build()

    override fun fetchJobs(params: SaraminFetchParams): List<SaraminJobDto> {
        require(apiKey.isNotBlank()) { "jobalert.saramin.api-key 미설정 — application-prod.yml 또는 SARAMIN_API_KEY 환경변수 확인" }

        val uri = buildUri(params)
        val started = System.currentTimeMillis()

        return try {
            val response = doGet(uri)
            val duration = (System.currentTimeMillis() - started).toInt()

            // 사람인은 200으로 application-level 에러를 줄 수 있음. body 검사.
            if (response.code != null) {
                logCall(params, statusCode = 200, durationMs = duration, errorMessage = "saramin code=${response.code} msg=${response.message}")
                throw SaraminApiException(
                    saraminCode = response.code,
                    httpStatus = 200,
                    message = "사람인 응답 오류: code=${response.code}, message=${response.message ?: "(no message)"}",
                )
            }

            val rawJobs = response.jobs?.job ?: emptyList()
            val domain = rawJobs.mapNotNull(SaraminMapper::toDomain)

            log.info(
                "saramin.fetchJobs ok start={} count={} total={} returned={} mapped={}",
                params.start, params.count, response.jobs?.total, rawJobs.size, domain.size,
            )
            logCall(params, statusCode = 200, durationMs = duration, resultCount = domain.size)
            domain
        } catch (ex: SaraminApiException) {
            throw ex
        } catch (ex: RestClientException) {
            val duration = (System.currentTimeMillis() - started).toInt()
            val httpStatus = parseHttpStatusFromMessage(ex.message)
            logCall(params, statusCode = httpStatus, durationMs = duration, errorMessage = ex.message)
            throw SaraminApiException(
                saraminCode = null,
                httpStatus = httpStatus ?: 0,
                message = "사람인 HTTP 호출 실패: ${ex.message}",
                cause = ex,
            )
        }
    }

    private fun doGet(uri: URI): SaraminApiResponse =
        restClient.get()
            .uri(uri)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError) { _, res ->
                val body = res.body.bufferedReader().readText()
                val parsed = runCatching { objectMapper.readValue(body, SaraminApiResponse::class.java) }.getOrNull()
                throw SaraminApiException(
                    saraminCode = parsed?.code,
                    httpStatus = res.statusCode.value(),
                    message = "사람인 4xx: status=${res.statusCode.value()} body=${body.take(300)}",
                )
            }
            .onStatus(HttpStatusCode::is5xxServerError) { _, res ->
                throw SaraminApiException(
                    saraminCode = null,
                    httpStatus = res.statusCode.value(),
                    message = "사람인 5xx: status=${res.statusCode.value()}",
                )
            }
            .body(SaraminApiResponse::class.java)
            ?: throw SaraminApiException(saraminCode = null, httpStatus = 0, message = "사람인 응답 body 비어있음")

    private fun buildUri(params: SaraminFetchParams): URI {
        val builder = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("access-key", apiKey)
            .queryParam("start", params.start)
            .queryParam("count", params.count.coerceIn(1, 110))
            .queryParam("sort", params.sort)
        params.publishedMin?.let { builder.queryParam("published_min", it) }
        params.publishedMax?.let { builder.queryParam("published_max", it) }
        params.keywords?.let { builder.queryParam("keywords", it) }
        params.indCd?.let { builder.queryParam("ind_cd", it) }
        params.sr?.let { builder.queryParam("sr", it) }
        params.fields?.let { builder.queryParam("fields", it) }
        return builder.build().encode().toUri()
    }

    private fun logCall(
        params: SaraminFetchParams,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int? = null,
        errorMessage: String? = null,
    ) {
        val paramsMap = buildMap<String, Any?> {
            put("start", params.start)
            put("count", params.count)
            put("sort", params.sort)
            params.publishedMin?.let { put("published_min", it) }
            params.indCd?.let { put("ind_cd", it) }
            params.sr?.let { put("sr", it) }
            resultCount?.let { put("result_count", it) }
        }
        apiCallLogger.log(
            source = "saramin",
            endpoint = "job-search",
            params = paramsMap,
            statusCode = statusCode,
            durationMs = durationMs,
            errorMessage = errorMessage,
        )
    }

    private fun timeoutRequestFactory(): ClientHttpRequestFactory =
        SimpleClientHttpRequestFactory().apply {
            setConnectTimeout(Duration.ofSeconds(3))
            setReadTimeout(Duration.ofSeconds(7))
        }

    private fun parseHttpStatusFromMessage(message: String?): Int? {
        if (message == null) return null
        // RestClientException 메시지에 종종 "404 NOT_FOUND" 같은 패턴 포함.
        return Regex("""\b(\d{3})\b""").find(message)?.groupValues?.get(1)?.toIntOrNull()
    }
}
