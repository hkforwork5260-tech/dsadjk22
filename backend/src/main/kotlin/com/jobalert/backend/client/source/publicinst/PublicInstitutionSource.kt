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
    @Value("\${jobalert.sources.public-institution.detail-url:https://apis.data.go.kr/1051000/recruitment/detail}") private val detailUrl: String,
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

    /**
     * 페이지(100건)마다 본문까지 받아 emit → 호출측이 즉시 적재하고 비운다. 메모리 피크를
     * '500건 전체'가 아니라 '100건'으로 낮춰 무료 박스 OOM을 회피한다. (전체 한 번에 들면 박스가 죽음.)
     */
    override fun fetchInBatches(onBatch: (List<RawJobPosting>) -> Unit) {
        if (serviceKey.isBlank()) {
            log.warn("public-institution serviceKey 미설정 — 수집 건너뜀.")
            return
        }
        for (page in 1..maxPages) {
            val batch = fetchPage(page) ?: break
            val raws = batch.mapNotNull(::toRawJob) // 이 페이지의 본문 포함 — 적재 후 GC 대상
            if (raws.isNotEmpty()) {
                log.info("public-institution page={} → onBatch {}건", page, raws.size)
                onBatch(raws)
            }
            if (batch.size < pageSize) break // 마지막 페이지
        }
        log.info("public-institution.fetchInBatches 완료")
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

        // 상세 조회로 본문(응시자격·전형방법·우대) + 학력 보강. best-effort: 실패해도 목록 정보로 적재.
        val detail = fetchDetail(sn)

        return RawJobPosting(
            source = sourceId,
            externalId = "pubinst-$sn",
            title = title,
            companyName = inst,
            location = job.workRgnNmLst,
            department = job.ncsCdNmLst,
            experience = job.recrutSeNm,   // 채용구분: 신입/경력/인턴 (구조화 데이터)
            postingDateEpoch = SourceUtil.yyyymmddToEpochSeconds(job.pbancBgngYmd),
            deadlineEpoch = SourceUtil.yyyymmddToEpochSeconds(job.pbancEndYmd, endOfDay = true),
            // JOB-ALIO 모바일 상세 직링크. 데스크톱 recruitview.do?idx= 는 폰에서 모바일 사이트로
            // 리다이렉트되며 idx를 잃고 검색목록으로 빠진다(실측). mobile2021 경로는 같은 idx로 공고에
            // 바로 간다(검증 2026-06-08). data.go.kr recrutPblntSn == ALIO idx.
            originalUrl = "https://job.alio.go.kr/mobile2021/recruit/recruitView.do?idx=$sn",
            keywords = listOfNotNull(job.recrutSeNm, job.hireTypeNmLst).filter { it.isNotBlank() },
            description = detail?.let(::buildDescription),
            education = detail?.acbgCondNmLst?.trim()?.takeIf { it.isNotBlank() },
        )
    }

    /** 상세 API 호출(공고당 1회). 공공누리라 본문 자유 활용. 실패 시 null(목록 정보로만 적재). */
    private fun fetchDetail(sn: Long): PublicInstitutionDetail? {
        val uri = UriComponentsBuilder.fromUriString(detailUrl)
            .queryParam("serviceKey", serviceKey)
            .queryParam("sn", sn)
            .queryParam("resultType", "json")
            .build(true)
            .toUri()
        return try {
            restClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("public-institution detail sn=$sn status=${res.statusCode.value()}")
                }
                .body(PublicInstitutionDetailResponse::class.java)
                ?.takeIf { it.resultCode == 200 }
                ?.result
        } catch (ex: RestClientException) {
            log.warn("public-institution detail sn={} 실패(본문 생략): {}", sn, ex.message)
            null
        }
    }

    /** 상세 텍스트 필드를 사람이 읽기 좋은 본문으로 조합. "없음"뿐인 섹션은 생략. */
    private fun buildDescription(d: PublicInstitutionDetail): String? {
        fun clean(s: String?): String? = s?.trim()?.takeIf { it.isNotBlank() && it != "없음" }
        val parts = buildList {
            clean(d.aplyQlfcCn)?.let { add("[응시자격]\n$it") }
            clean(d.scrnprcdrMthdExpln)?.let { add("[전형방법]\n$it") }
            clean(d.prefCn)?.let { add("[우대사항]\n$it") }
            clean(d.disqlfcRsn)?.let { add("[결격사유]\n$it") }
        }
        return parts.joinToString("\n\n").takeIf { it.isNotBlank() }
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
