package com.jobalert.backend.client.source.naver

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil

/**
 * 네이버 loadJobList 응답 → [RawJobPosting] 매퍼 (순수 함수).
 * 날짜는 "yyyy.MM.dd HH:mm:ss"라 앞 10자(날짜)만 잘라 KST로 변환.
 */
object NaverMapper {

    private fun datePart(s: String?): String? = s?.trim()?.takeIf { it.length >= 10 }?.substring(0, 10)

    /** "NAVER"는 "네이버"로, 계열사명은 그대로. */
    private fun companyName(raw: String?): String = when (val n = raw?.trim()) {
        null, "" -> "네이버"
        "NAVER" -> "네이버"
        else -> n
    }

    fun toRawJob(j: NaverJob): RawJobPosting? {
        val id = j.annoId ?: return null
        val title = j.annoSubject?.trim()?.takeIf { it.isNotBlank() } ?: return null

        return RawJobPosting(
            source = "naver",
            externalId = "naver-$id",
            title = title,
            companyName = companyName(j.sysCompanyCdNm),
            companyHomepage = "navercorp.com",
            location = "한국",
            department = j.classCdNm?.trim()?.takeIf { it.isNotBlank() },
            experience = j.entTypeCdNm?.trim()?.takeIf { it.isNotBlank() },
            postingDateEpoch = SourceUtil.dottedDateToEpochSeconds(datePart(j.staYmdTime)),
            deadlineEpoch = SourceUtil.dottedDateToEpochSeconds(datePart(j.endYmdTime), endOfDay = true),
            originalUrl = j.jobDetailLink?.takeIf { it.isNotBlank() }
                ?: "https://recruit.navercorp.com/rcrt/view.do?annoId=$id",
            keywords = listOfNotNull(j.classCdNm, j.subJobCdNm).map { it.trim() }.filter { it.isNotBlank() }.distinct(),
        )
    }
}
