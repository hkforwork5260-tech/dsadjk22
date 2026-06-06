package com.jobalert.backend.client.source.seoul

import com.fasterxml.jackson.databind.ObjectMapper
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
import java.time.Duration
import java.time.OffsetDateTime

/**
 * 서울시 일자리포털 채용정보 소스.
 *
 * 인증: data.seoul.go.kr serviceKey(환경변수 `JOBALERT_SEOUL_KEY`). 테스트는 sample 키로 일부 가능.
 * 서울 소재 중소·중견 위주(2026-06-06 총 23,145건). 본문·급여·마감일 제공. 공공누리 1유형(상업 OK).
 *
 * "최근 진행중 위주": 최신 등록순 앞에서 max-rows건만 받아, 마감 지난 공고는 제외한다.
 * 활성화: `jobalert.sources.seoul.enabled=true` + service-key.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.seoul.enabled"], havingValue = "true")
class SeoulJobSource(
    private val apiCallLogger: ApiCallLogger,
    private val objectMapper: ObjectMapper,
    @Value("\${jobalert.sources.seoul.service-key:sample}") private val serviceKey: String,
    @Value("\${jobalert.sources.seoul.base-url:http://openapi.seoul.go.kr:8088}") private val baseUrl: String,
    @Value("\${jobalert.sources.seoul.max-rows:1000}") private val maxRows: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "seoul"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("User-Agent", USER_AGENT)
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        // 서울 API는 1회 최대 1000건. max-rows를 1000 단위로 끊어 호출.
        val now = OffsetDateTime.now()
        val all = mutableListOf<RawJobPosting>()
        var start = 1
        while (start <= maxRows) {
            val end = minOf(start + PAGE - 1, maxRows)
            val batch = fetchRange(start, end) ?: break
            all += batch.mapNotNull { toRawJob(it, now) }
            if (batch.size < (end - start + 1)) break // 마지막
            start += PAGE
        }
        log.info("seoul.fetchAll collected(진행중)={}", all.size)
        return all
    }

    private fun fetchRange(start: Int, end: Int): List<SeoulJob>? {
        val url = "$baseUrl/$serviceKey/json/GetJobInfo/$start/$end/"
        val started = System.currentTimeMillis()
        return try {
            // 서울 API는 본문이 JSON인데 Content-Type을 application/xml로 잘못 보냄 →
            // 메시지 컨버터가 못 고르므로 String으로 받아 직접 파싱.
            val rawBody = restClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, r ->
                    throw RestClientException("seoul $start-$end status=${r.statusCode.value()}")
                }
                .body(String::class.java)
            val res = rawBody?.let { objectMapper.readValue(it, SeoulJobResponse::class.java) }
            val body = res?.getJobInfo
            val code = body?.result?.code
            val duration = (System.currentTimeMillis() - started).toInt()
            if (code != "INFO-000") {
                logCall(start, statusCode = null, durationMs = duration, errorMessage = code)
                log.warn("seoul {}-{} code={} msg={}", start, end, code, body?.result?.message)
                return null
            }
            val rows = body.row
            logCall(start, statusCode = 200, durationMs = duration, resultCount = rows.size)
            log.info("seoul {}-{} fetched={} total={}", start, end, rows.size, body.listTotalCount)
            rows
        } catch (ex: Exception) {
            val duration = (System.currentTimeMillis() - started).toInt()
            logCall(start, statusCode = null, durationMs = duration, errorMessage = ex.message)
            log.warn("seoul {}-{} 실패(중단): {}", start, end, ex.message)
            null
        }
    }

    private fun toRawJob(job: SeoulJob, now: OffsetDateTime): RawJobPosting? {
        val id = job.joReqstNo?.takeIf { it.isNotBlank() } ?: return null
        val title = job.joSj?.takeIf { it.isNotBlank() } ?: return null
        val company = job.cmpnyNm?.takeIf { it.isNotBlank() } ?: return null

        val deadlineEpoch = parseDeadline(job.rceptClosNm)
        // 최근 진행중 위주: 마감일이 있고 이미 지났으면 제외. 마감 없음(상시)은 포함.
        if (deadlineEpoch != null && deadlineEpoch < now.toEpochSecond()) return null

        // 노인·중장년 전용 일자리(요양·경비·청소 등)는 타겟(취준생)과 안 맞아 제외. (사용자 결정 2026-06-06)
        if (isElderlyOrCareJob(title, job.dtyCn)) return null

        return RawJobPosting(
            source = sourceId,
            externalId = "seoul-$id",
            title = title,
            companyName = company,
            location = (job.workAddr ?: job.bassAddr)?.takeIf { it.isNotBlank() },
            department = job.jobcodeNm?.takeIf { it.isNotBlank() },
            experience = job.careerCndNm?.takeIf { it.isNotBlank() },
            postingDateEpoch = SourceUtil.yyyymmddToEpochSeconds(job.joRegDt?.replace("-", "")),
            deadlineEpoch = deadlineEpoch,
            originalUrl = JOB_PORTAL_SEARCH,
            keywords = listOfNotNull(job.jobcodeNm, job.emplymStle, job.careerCndNm).filter { it.isNotBlank() },
            description = buildDescription(job),
            education = clean(job.acdmcrNm),
            salary = clean(job.hopeWage),
            tags = buildTags(job),
        )
    }

    /** 구조화 정보(고용형태·휴일·근무시간대·4대보험·퇴직금)를 카드용 짧은 태그로. */
    private fun buildTags(job: SeoulJob): List<String> = buildList {
        cleanTag(job.emplymStle)?.let { add(it) }                    // 정규직/상용직 등(괄호 앞)
        job.holidayNm?.let { h ->
            when {
                h.contains("주5일") -> add("주5일")
                h.contains("주6일") -> add("주6일")
                h.contains("주4일") -> add("주4일")
            }
        }
        cleanTag(job.workTmNm)?.let { add(it) }                      // 주간/야간
        val ins = job.insuranceNm.orEmpty()
        if (ins.contains("고용") && ins.contains("산재") && ins.contains("건강") && ins.contains("국민")) add("4대보험")
        if (job.retGrantsNm?.contains("퇴직") == true) add("퇴직금")
    }.distinct().take(5)

    /** 괄호 앞부분만 + 빈/플레이스홀더 제거. "상용직(시간제)" → "상용직". */
    private fun cleanTag(s: String?): String? =
        s?.substringBefore("(")?.trim()?.takeIf { it.isNotBlank() && it != "-" }

    /** 노인·중장년 전용 일자리인가(요양·간병·경비·미화·청소·가사 등). 제목/본문 키워드로 판정. */
    private fun isElderlyOrCareJob(title: String, dtyCn: String?): Boolean {
        val hay = title + " " + (dtyCn ?: "")
        return EXCLUDE_KEYWORDS.any { hay.contains(it) }
    }

    /** "마감일 (2026-08-04)" 등에서 날짜 추출 → KST 그날 끝. 없으면(상시/채용시) null. */
    private fun parseDeadline(s: String?): Long? {
        if (s.isNullOrBlank()) return null
        val m = DATE_RE.find(s) ?: return null
        return SourceUtil.yyyymmddToEpochSeconds(m.groupValues[1] + m.groupValues[2] + m.groupValues[3], endOfDay = true)
    }

    /** 업무내용 + 접수방법·제출서류·문의처를 본문으로 조합(서울 공고는 본문에 지원정보가 있음). */
    private fun buildDescription(job: SeoulJob): String? {
        val parts = buildList {
            clean(job.dtyCn)?.let { add(it) }
            clean(job.emplymStle)?.let { add("[고용형태] $it") }
            clean(job.rceptMthNm)?.let { add("[접수방법] $it") }
            clean(job.presentnPapersNm)?.let { add("[제출서류] $it") }
            clean(job.mngrPhonNo)?.let { add("[문의] $it") }
        }
        return parts.joinToString("\n\n").takeIf { it.isNotBlank() }
    }

    /** 빈 값·플레이스홀더("-") 제거. */
    private fun clean(s: String?): String? = s?.trim()?.takeIf { it.isNotBlank() && it != "-" }

    private fun logCall(start: Int, statusCode: Int?, durationMs: Int, resultCount: Int? = null, errorMessage: String? = null) {
        apiCallLogger.log(
            source = sourceId,
            endpoint = "GetJobInfo",
            params = buildMap<String, Any?> {
                put("start", start)
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
        private const val PAGE = 1000 // 서울 API 1회 최대
        private const val USER_AGENT = "JobAlert/0.1 (job-alert app; contact: lhgdlagusurd@naver.com)"
        private const val JOB_PORTAL_SEARCH = "https://job.seoul.go.kr/hmpg/rmim/rsmg/rsmgListPage.do"
        private val DATE_RE = Regex("""(\d{4})-(\d{2})-(\d{2})""")

        /** 노인·중장년 전용 일자리 제외 키워드(제목/본문). 취준생 타겟과 안 맞는 직무. */
        private val EXCLUDE_KEYWORDS = listOf(
            "요양", "재가", "어르신", "할머니", "할아버지", "방문요양", "돌봄", "간병", "노인", "시니어",
            "경비", "미화", "청소", "환경미화", "가사", "산모", "베이비", "입주",
        )
    }
}
