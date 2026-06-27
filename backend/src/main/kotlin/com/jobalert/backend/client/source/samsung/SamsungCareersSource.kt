package com.jobalert.backend.client.source.samsung

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
 * 삼성 채용 통합 허브(`samsungcareers.com`) 소스.
 *
 * 삼성전자·전기·SDI·E&A·제일기획·에스원·삼성생명/화재·글로벌리서치 등 **계열사 공고가 한 곳에
 * 섞여** 나오는 통합 채용 사이트. 공채 시즌엔 삼성전자 신입 대규모 공채가 여기 올라온다.
 * robots.txt 없음, 인증 불필요(2026-06-27 정찰 + curl 재현 검증).
 *
 * 엔드포인트: `POST /hr/list.data` (form-urlencoded). 응답은 JSON이 아니라 **HTML 조각** —
 * `<li>` 한 개가 공고 하나. Jsoup으로 파싱한다.
 *  - `input.divCnt[data-value]` : 총 공고 수
 *  - `a[data-value]` : 공고번호(콤마 포함, 예 "22,584" → 22584). 원문 직링크 키.
 *  - `p.company` : 계열사명  /  `h3.title` : 제목
 *  - `p.info > span:first` : 신입/경력  /  `span.period` : "2026.06.26 ~ 2026.07.13"(시작 ~ 마감)
 *  - `div.flagWrap span.flag.grey` : 직무 태그(여러 개)
 *
 * best-effort: 페이지 하나가 실패해도 그때까지 모은 건 반환.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.sources.samsung.enabled"], havingValue = "true", matchIfMissing = true)
class SamsungCareersSource(
    private val apiCallLogger: ApiCallLogger,
    @Value("\${jobalert.sources.samsung.base-url:https://www.samsungcareers.com/hr}") private val baseUrl: String,
    // 폭주 방지 캡. 한 페이지에 전부 담겨오는 경우가 많아 사실상 1~2페이지면 끝.
    @Value("\${jobalert.sources.samsung.max-pages:10}") private val maxPages: Int,
) : JobSource {

    private val log = LoggerFactory.getLogger(javaClass)

    override val sourceId = "samsung"

    private val restClient: RestClient = RestClient.builder()
        .requestFactory(timeoutRequestFactory())
        .defaultHeader("User-Agent", USER_AGENT)
        .defaultHeader("X-Requested-With", "XMLHttpRequest")
        .defaultHeader("Referer", "$baseUrl/")
        .build()

    override fun fetchAll(): List<RawJobPosting> {
        val all = mutableListOf<RawJobPosting>()
        var page = 1
        while (page <= maxPages) {
            val (items, total) = fetchPage(page)
            if (items.isEmpty()) break
            all += items
            // 누적이 총건수 도달했으면 종료(대개 1페이지에 전부 담김).
            if (total > 0 && all.size >= total) break
            page++
        }
        log.info("samsung.fetchAll collected={}", all.size)
        return all
    }

    /** @return (이 페이지 공고들, 총 공고 수). 실패하면 (빈 리스트, 0). */
    private fun fetchPage(page: Int): Pair<List<RawJobPosting>, Int> {
        val url = "$baseUrl/list.data"
        val body = "currentPageNo=$page&intNo=0&strVal=&strTxt=&strKey=&strCompany=&strType=&strOrderBy=&strEntity="
        val started = System.currentTimeMillis()
        return try {
            val html = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError) { _, res ->
                    throw RestClientException("samsung list.data status=${res.statusCode.value()}")
                }
                .body(String::class.java) ?: ""

            val (items, total) = SamsungListParser.parse(html, baseUrl)
            val duration = (System.currentTimeMillis() - started).toInt()
            apiCallLogger.log(
                source = sourceId,
                endpoint = "list.data",
                params = mapOf("page" to page, "result_count" to items.size, "total" to total),
                statusCode = 200,
                durationMs = duration,
            )
            log.info("samsung page={} parsed={} total={}", page, items.size, total)
            items to total
        } catch (ex: RestClientException) {
            val duration = (System.currentTimeMillis() - started).toInt()
            apiCallLogger.log(
                source = sourceId,
                endpoint = "list.data",
                params = mapOf("page" to page),
                statusCode = null,
                durationMs = duration,
                errorMessage = ex.message,
            )
            log.warn("samsung page={} 실패(스킵): {}", page, ex.message)
            emptyList<RawJobPosting>() to 0
        }
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
