package com.jobalert.backend.client.source.lx

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil
import org.jsoup.Jsoup

/**
 * LX 그룹 통합 채용(apply.lxcareers.com) 목록 HTML → [RawJobPosting] 파서 (순수 함수, 단위 테스트 용이).
 *
 * 응답은 `RetrieveJobNoticesList.rpi`의 테이블 조각. 각 공고 행(`tr`)에:
 *  - `span.Lhide` = "[계열사] 제목" (예: "[LX세미콘] 2026년 6월 경력사원 채용")
 *  - `input[name=jobNoticeId]` = 공고 ID
 *  - 행 텍스트 어딘가 "yyyy.MM.dd HH:mm" 마감일시
 * 계열사(LX하우시스·세미콘·인터내셔널·판토스)가 한 응답에 통합돼 옴.
 */
object LxListParser {

    private val BRACKET = Regex("""^\s*\[([^\]]+)]\s*(.*)$""")
    private val DATE = Regex("""(\d{4}\.\d{2}\.\d{2})""")
    private val EXPERIENCE_TAGS = listOf("신입", "경력", "인턴")

    fun parse(html: String): List<RawJobPosting> {
        val doc = Jsoup.parse(html)
        val out = mutableListOf<RawJobPosting>()
        for (span in doc.select("span.Lhide")) {
            val tr = span.parents().firstOrNull { it.tagName() == "tr" } ?: continue
            val id = tr.selectFirst("input[name=jobNoticeId]")?.attr("value")?.trim()
                ?.takeIf { it.isNotBlank() } ?: continue

            val raw = span.text().trim()
            val m = BRACKET.find(raw)
            // "[LX판토스/현지채용]"처럼 슬래시 뒤는 채용유형 → 회사명은 슬래시 앞만.
            val company = m?.groupValues?.get(1)?.substringBefore("/")?.trim()?.takeIf { it.isNotBlank() }
            val title = m?.groupValues?.get(2)?.trim()?.takeIf { it.isNotBlank() } ?: raw
            if (title.isBlank()) continue

            val deadline = DATE.find(tr.text())?.groupValues?.get(1)
                ?.let { SourceUtil.dottedDateToEpochSeconds(it, endOfDay = true) }
            val experience = EXPERIENCE_TAGS.firstOrNull { raw.contains(it) }

            out += RawJobPosting(
                source = "lx",
                externalId = "lx-$id",
                title = title,
                companyName = company ?: "LX",
                location = "한국",
                experience = experience,
                deadlineEpoch = deadline,
                originalUrl = "https://apply.lxcareers.com/app/job/RetrieveJobNoticesDetail.rpi?jobNoticeId=$id",
            )
        }
        return out
    }
}
