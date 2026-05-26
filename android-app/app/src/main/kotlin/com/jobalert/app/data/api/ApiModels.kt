package com.jobalert.app.data.api

/**
 * API_CONTRACT.md 의 JSON 형식을 Kotlin data class로 미러링.
 * 백엔드 연동 시 Retrofit + kotlinx.serialization 로 deserialize 가능.
 *
 * v0.1 mock 단계에서는 [MockApi]가 이 객체들을 직접 생성해서 반환.
 */

/** 공통 Company 요약/상세 공용 */
data class CompanyDto(
    val id: Int,
    val name: String,
    val logo: String,                    // 로고용 짧은 텍스트 ("삼성")
    val logoUrl: String? = null,         // Clearbit URL (v0.1 미사용)
    val industry: String = "",
    val group: String = "",
    val size: String = "",               // "large_corp" | "mid_corp" | ...
    val homepageUrl: String? = null,
    val careersUrl: String? = null,
    val activeJobCount: Int = 0,
    val isFavorited: Boolean = false,
    val description: String? = null,
)

/** Job 요약형 (카드용) */
data class JobDto(
    val id: String,
    val company: CompanyDto,
    val title: String,
    val kind: String,                    // "NEW" | "UPDATE" | "CLOSING" | "EXPIRED"
    val dday: String,
    val deadline: String,                // ISO8601
    val location: String = "",
    val experience: String = "",
    val education: String = "",
    val tags: List<String> = emptyList(),
    val isFavorited: Boolean = false,
)

/** JobDetail 상세형 */
data class JobDetailDto(
    val id: String,
    val company: CompanyDto,
    val title: String,
    val kind: String,
    val dday: String,
    val deadline: String,
    val postingDate: String,
    val location: String,
    val experience: String,
    val education: String,
    val salary: String = "회사내규",
    val jobCategories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val description: String = "",
    val summary: String = "",
    val preferred: List<String> = emptyList(),
    val process: List<String> = emptyList(),
    val originalUrl: String = "",
    val source: String = "saramin",
    val isFavorited: Boolean = false,
)

/** GET /jobs/today */
data class JobsTodayResponse(
    val date: String,
    val counts: Map<String, Int>,        // {"new":6, "update":2, "closing":1}
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

/** GET /jobs/search */
data class JobsSearchResponse(
    val query: String,
    val totalEstimate: Int,
    val companies: List<CompanyDto> = emptyList(),
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

/** GET /onboarding/categories */
data class JobCategoryDto(val code: String, val label: String)
data class CategoriesResponse(val categories: List<JobCategoryDto>)

/** GET /onboarding/popular-companies */
data class PopularCompaniesResponse(val companies: List<CompanyDto>)
