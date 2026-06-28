package com.jobalert.backend.client.source.toss

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil

/**
 * 토스 job-groups 응답 → [RawJobPosting] 매퍼 (순수 함수).
 * 회사명은 metadata "소속 자회사"(토스/토스뱅크/토스증권 등) 우선, 직군은 "Job Category".
 */
object TossMapper {

    private fun metaValue(p: TossPrimaryJob, nameContains: String): String? =
        p.metadata.firstOrNull { it.name?.contains(nameContains) == true }
            ?.value?.let { it as? String }?.trim()?.takeIf { it.isNotBlank() }

    fun toRawJob(g: TossJobGroup): RawJobPosting? {
        val id = g.id ?: return null
        val p = g.primaryJob ?: return null
        val title = (p.title ?: g.title)?.trim()?.takeIf { it.isNotBlank() } ?: return null

        val subsidiary = metaValue(p, "자회사")
        val category = metaValue(p, "Job Category")
        val employment = metaValue(p, "Employment_Type")
        val experience = if (employment?.contains("인턴") == true) "인턴" else null

        return RawJobPosting(
            source = "toss",
            externalId = "toss-$id",
            title = title,
            companyName = subsidiary ?: "토스",
            companyHomepage = "toss.im",
            location = p.location?.name?.trim()?.takeIf { it.isNotBlank() } ?: "한국",
            department = category,
            experience = experience,
            postingDateEpoch = SourceUtil.isoToEpochSeconds(p.firstPublished),
            deadlineEpoch = SourceUtil.isoToEpochSeconds(p.applicationDeadline),
            originalUrl = p.absoluteUrl ?: "https://toss.im/career/job-detail?gh_jid=$id",
            keywords = listOfNotNull(category),
        )
    }
}
