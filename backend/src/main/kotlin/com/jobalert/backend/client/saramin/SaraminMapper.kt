package com.jobalert.backend.client.saramin

/**
 * 사람인 raw response를 도메인 DTO로 변환.
 */
object SaraminMapper {

    fun toDomain(raw: SaraminJobRaw): SaraminJobDto? {
        val id = raw.id?.takeIf { it.isNotBlank() } ?: return null
        val companyName = raw.company?.detail?.name?.takeIf { it.isNotBlank() } ?: return null
        val title = raw.position?.title?.takeIf { it.isNotBlank() } ?: return null

        return SaraminJobDto(
            externalId = "saramin-$id",
            title = title,
            companyName = companyName,
            companyHomepage = raw.company.detail.href,
            industry = raw.position.industry?.name,
            location = raw.position.location?.name,
            experience = raw.position.experienceLevel?.name,
            education = raw.position.requiredEducationLevel?.name,
            salary = raw.salary?.name,
            postingDateEpoch = raw.postingTimestamp?.toLongOrNull(),
            deadlineEpoch = raw.expirationTimestamp?.toLongOrNull(),
            originalUrl = raw.url,
            keywords = raw.keyword
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?: emptyList(),
        )
    }
}
