package com.jobalert.backend.client.source.workday

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.WorkdayTenant

/**
 * Workday jobPostings 응답 → [RawJobPosting] 매퍼 (순수 함수).
 * 게시일(postedOn)은 "Posted Yesterday" 같은 상대표현이라 날짜는 채우지 않는다.
 */
object WorkdayMapper {

    /** 외부ID 키: bulletFields 첫 값(R-26-1815) 우선, 없으면 externalPath 마지막 세그먼트. */
    private fun externalKey(p: WorkdayJobPosting): String? {
        p.bulletFields.firstOrNull { it.isNotBlank() }?.let { return it.trim() }
        return p.externalPath?.trim('/')?.substringAfterLast('/')?.takeIf { it.isNotBlank() }
    }

    fun toRawJob(t: WorkdayTenant, p: WorkdayJobPosting): RawJobPosting? {
        val title = p.title?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val path = p.externalPath?.trim()?.takeIf { it.isNotBlank() } ?: return null
        val key = externalKey(p) ?: return null

        return RawJobPosting(
            source = "workday",
            externalId = "workday-${t.cxsTenant}-$key",
            title = title,
            companyName = t.displayName,
            companyHomepage = t.homepage,
            location = p.locationsText?.trim()?.takeIf { it.isNotBlank() } ?: "한국",
            originalUrl = "https://${t.host}.myworkdayjobs.com/${t.site}$path",
            keywords = listOfNotNull(p.timeType?.trim()?.takeIf { it.isNotBlank() }),
        )
    }
}
