package com.jobalert.backend.client.source.samsung

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * 삼성 채용 `list.data` HTML 조각 파서 (순수 함수, 의존성 없음 → 단위 테스트 용이).
 *
 * 응답 형태: `<input class="divCnt" data-value="총건수">` + 공고당 `<li>` 반복.
 */
object SamsungListParser {

    /** @return (파싱된 공고들, 총 공고 수). 총건수 없으면 0. */
    fun parse(html: String, baseUrl: String): Pair<List<RawJobPosting>, Int> {
        val doc = Jsoup.parse(html)
        val total = doc.selectFirst("input.divCnt")?.attr("data-value")
            ?.replace(",", "")?.toIntOrNull() ?: 0
        val items = doc.select("li").mapNotNull { toRawJob(it, baseUrl) }
        return items to total
    }

    private fun toRawJob(li: Element, baseUrl: String): RawJobPosting? {
        // 공고번호: a[data-value]="22,584" → "22584". 숫자가 아니면 공고 li가 아님(스킵).
        val anchor = li.selectFirst("a[data-value]") ?: return null
        val no = anchor.attr("data-value").replace(",", "").trim()
        if (no.isBlank() || no.toLongOrNull() == null) return null

        val title = li.selectFirst("h3.title")?.text()?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val company = li.selectFirst("p.company")?.text()?.trim()?.takeIf { it.isNotBlank() } ?: "삼성"

        val experience = li.selectFirst("p.info > span")?.text()?.trim()?.takeIf { it.isNotBlank() }

        // period "2026.06.26 ~ 2026.07.13" → 시작=등록일, 끝=마감일(그날 23:59:59).
        val period = li.selectFirst("span.period")?.text()?.trim().orEmpty()
        val parts = period.split("~").map { it.trim() }
        val postingDateEpoch = SourceUtil.dottedDateToEpochSeconds(parts.getOrNull(0))
        val deadlineEpoch = SourceUtil.dottedDateToEpochSeconds(parts.getOrNull(1), endOfDay = true)

        // grey 플래그 = 직무 태그(여러 개). 표시 칩(tags) + 분류기 입력(keywords) 둘 다에.
        val jobFlags = li.select("div.flagWrap span.flag.grey").map { it.text().trim() }.filter { it.isNotBlank() }

        return RawJobPosting(
            source = "samsung",
            externalId = "samsung-$no",
            title = title,
            companyName = company,
            location = "한국",
            experience = experience,
            postingDateEpoch = postingDateEpoch,
            deadlineEpoch = deadlineEpoch,
            originalUrl = "$baseUrl/?no=$no",
            keywords = jobFlags,
            tags = jobFlags,
        )
    }
}
