package com.jobalert.backend.client.source.cj

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil

/**
 * CJ searchNewGonggoList 응답 → [RawJobPosting] 매퍼 (순수 함수).
 */
object CjMapper {

    fun toRawJob(j: CjJob): RawJobPosting? {
        val joNum = j.joNum?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val title = j.title?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val company = j.company?.trim()?.takeIf { it.isNotBlank() } ?: "CJ"

        return RawJobPosting(
            source = "cj",
            externalId = "cj-$joNum",
            title = title,
            companyName = company,
            location = j.location?.trim()?.takeIf { it.isNotBlank() } ?: "한국",
            department = j.jobName?.trim()?.takeIf { it.isNotBlank() },
            postingDateEpoch = SourceUtil.millisToEpochSeconds(j.startDtMs),
            deadlineEpoch = SourceUtil.millisToEpochSeconds(j.endDtMs),
            originalUrl = "https://recruit.cj.net/recruit/ko/recruit/recruit/detail.fo?zz_jo_num=$joNum",
            keywords = listOfNotNull(j.jobName?.trim(), j.location?.trim()).filter { it.isNotBlank() }.distinct(),
        )
    }
}
