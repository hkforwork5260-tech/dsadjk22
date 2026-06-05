package com.jobalert.backend.client.source.publicinst

import com.jobalert.backend.client.source.JobSource
import com.jobalert.backend.client.source.RawJobPosting
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
import org.springframework.web.util.UriComponentsBuilder
import java.time.Duration

/**
 * 기재부 공공기관 채용정보 소스.
 *
 * 인증: data.go.kr serviceKey 필요(환경변수 `JOBALERT_PUBINST_KEY`). 키 없으면 빈 결과.
 * 전국 공공기관 채용공고 실시간(2026-06-05 기준 11만 건). 마감일·원본URL·신입/경력 제공.
 *
 * 활성화: `jobalert.sources.public-institution.enabled=true` + service-key 설정.
 * best-effort: 페이지 하나 실패해도 모은 만큼 반환.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.public-institution.enabled"], havingValue = "true")
class PublicInstitutionSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.public-institution.service-key:}") private val serviceKey: String,
    @Value("\${jobalert.sources.public-institution.base-url:https://apis.data.go.kr/1051000/recruitment/list}") private val baseUrl: String,
    @Value("\${jobalert.sources.public-institution.max-pages:5}") private val maxPages: Int,
    @Value("\${jobalert.sources.public-institution.page-size:100}") private val pageSize: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "public-institution"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        if (serviceKey.isBlank()) {
            log.warn("public-institution serviceKey 미설정 — JOBALERT_PUBINST_KEY 환경변수 확인. 수집 건너뜀.")
            return emptyList()
        }

        val all = mutableListOf<RawJobPosting>()
        for (page in 1..maxPages) {
            val batch = fetchPage(page) ?: break
            all += batch.mapNotNull(::toRawJob)
            if (batch.size < pageSize) break // 마지막 페이지
        }
        log.info("public-institution.fetchAll collected={}", all.size)
        return all
    }

    private fun fetchPage(page: Int): List<PublicInstitutionJob>? {
        // serviceKey는 hex(특수문자 없음)라 추가 인코딩 불필요. build(true)로 이중 인코딩 방지.
        val uri = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("serviceKey", serviceKey)
            .queryParam("pageNo", page)
            .queryParam("numOfRows", pageSize)
            .queryParam("resultType", "json")
            .build(true)
            .toUri()

        val started = System.currentTimeMillis()
        return try {
            val response = restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("public-institution page=$page status=${res.statusCode.value()}")
                }
                .body(PublicInstitutionResponse::class.java)

            val duration = (System.currentTimeMillis() - started).toInt()
            if (response?.resultCode != 200) {
                logCall(page, statusCode = response?.resultCode, durationMs = duration, errorMessage = response?.resultMsg)
                log.warn("public-institution page={} resultCode={} msg={}", page, response?.resultCode, response?.resultMsg)
                return null
            }
            val jobs = response.result
            logCall(page, statusCode = 200, durationMs = duration, resultCount = jobs.size)
            log.info("public-institution page={} fetched={} total={}", page, jobs.size, response.totalCount)
            jobs
        } catch (ex: RestClientException) {
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(page, statusCode = null, durationMs = duration, errorMessage = ex.message)
            log.warn("public-institution page={} 실패(중단): {}", page, ex.message)
            null
        }
    }

    private fun toRawJob(job: PublicInstitutionJob): RawJobPosting? {
        val sn = job.recrutPblntSn ?: return null
        val title = job.recrutPbancTtl?.takeIf { it.isNotBlank() } ?: return null
        val inst = job.instNm?.takeIf { it.isNotBlank() } ?: return null

        return RawJobPosting(
            source = sourceId,
            externalId = "pubinst-$sn",
            title = title,
            companyName = inst,
            location = job.workRgnNmLst,
            department = job.ncsCdNmLst,
            postingDateEpoch = SourceUtil.yyyymmddToEpochSeconds(job.pbancBgngYmd),
            deadlineEpoch = SourceUtil.yyyymmddToEpochSeconds(job.pbancEndYmd, endOfDay = true),
            originalUrl = job.srcUrl,
            keywords = listOfNotNull(job.recrutSeNm, job.hireTypeNmLst).filter { it.isNotBlank() },
        )
    }

    private fun logCall(
        page: Int,
        statusCode: Int?,
        durationMs: Int,
        resultCount: Int? = null,
        errorMessage: String? = null,
    ) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "recruitment/list",
            params = buildMap<String, Any?> {
                put("pageNo", page)
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
