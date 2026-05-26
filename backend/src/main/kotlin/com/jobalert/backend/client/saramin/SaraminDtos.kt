package com.jobalert.backend.client.saramin

/**
 * 사람인 OpenAPI 응답 형태 (raw).
 * 실제 응답 스키마는 https://oapi.saramin.co.kr/job-search 문서 참고.
 * 여기는 v0.1에서 필요한 필드만 추림.
 */
data class SaraminFetchParams(
    val coSize: String? = null,        // 'large,mid,small,public'
    val keywords: String? = null,
    val start: Int = 1,
    val count: Int = 100,
)

data class SaraminJobDto(
    val externalId: String,
    val title: String,
    val companyName: String,
    val companyHomepage: String? = null,
    val industry: String? = null,
    val location: String? = null,
    val experience: String? = null,
    val education: String? = null,
    val salary: String? = null,
    val postingDateEpoch: Long? = null,
    val deadlineEpoch: Long? = null,
    val originalUrl: String? = null,
    val keywords: List<String> = emptyList(),
)
