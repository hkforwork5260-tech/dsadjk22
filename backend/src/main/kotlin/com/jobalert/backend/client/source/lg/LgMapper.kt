package com.jobalert.backend.client.source.lg

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil

/**
 * LG retrieveJobNoticesList 응답 → [RawJobPosting] 매퍼 (순수 함수).
 * 마감은 "yyyy.MM.dd HH:mm"라 앞 10자만 잘라 KST 마감으로 변환.
 */
object LgMapper {

    private fun datePart(s: String?): String? = s?.trim()?.takeIf { it.length >= 10 }?.substring(0, 10)

    fun toRawJob(n: LgJobNotice): RawJobPosting? {
        val id = n.jobNoticeId ?: return null
        val title = n.jobNoticeName?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val company = n.companyName?.trim()?.takeIf { it.isNotBlank() } ?: "LG"

        return RawJobPosting(
            source = "lg",
            externalId = "lg-$id",
            title = title,
            companyName = company,
            location = "한국",
            department = n.jobGroupName?.trim()?.takeIf { it.isNotBlank() },
            experience = n.careerTypeName?.trim()?.takeIf { it.isNotBlank() },
            deadlineEpoch = SourceUtil.dottedDateToEpochSeconds(datePart(n.recEndDateTime), endOfDay = true),
            originalUrl = "https://careers.lg.com/apply/detail?id=$id",
            keywords = listOfNotNull(n.jobGroupName?.trim()?.takeIf { it.isNotBlank() }),
        )
    }
}
