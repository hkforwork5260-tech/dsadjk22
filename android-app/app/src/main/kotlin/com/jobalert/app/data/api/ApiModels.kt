package com.jobalert.app.data.api

import kotlinx.serialization.Serializable

/**
 * API_CONTRACT.md 의 JSON 형식을 Kotlin data class로 미러링.
 *
 * 백엔드(Jackson)는 snake_case로 직렬화하고, 여기 프로퍼티는 camelCase다.
 * → [ApiClient]의 Json 설정에서 `JsonNamingStrategy.SnakeCase`로 전역 매핑하므로
 *   필드마다 @SerialName을 붙일 필요 없다. (logoUrl ↔ logo_url 자동)
 *
 * 누락 필드 주의: 백엔드는 null 필드를 JSON에서 아예 빼버린다(non_null inclusion).
 * → null 가능 필드는 nullable로(deadline 등), 그 외는 기본값을 둬서 누락돼도 안전하게.
 */

/** 공통 Company 요약/상세 공용 */
@Serializable
data class CompanyDto(
    val id: Int,
    val name: String,
    val logo: String,                    // 로고용 짧은 텍스트 ("삼성")
    val logoUrl: String? = null,         // Clearbit URL
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
@Serializable
data class JobDto(
    val id: String,
    val company: CompanyDto,
    val title: String,
    val kind: String,                    // "NEW" | "UPDATE" | "CLOSING" | "EXPIRED"
    val dday: String,
    val deadline: String? = null,        // ISO8601. 상시채용(Greenhouse 등)은 null
    val location: String = "",
    val experience: String = "",
    val education: String = "",
    val tags: List<String> = emptyList(),
    val isFavorited: Boolean = false,
)

/** JobDetail 상세형 */
@Serializable
data class JobDetailDto(
    val id: String,
    val company: CompanyDto,
    val title: String,
    val kind: String,
    val dday: String,
    val deadline: String? = null,
    val postingDate: String? = null,
    val location: String = "",
    val experience: String = "",
    val education: String = "",
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
@Serializable
data class JobsTodayResponse(
    val date: String,
    val counts: Map<String, Int>,        // {"new":6, "update":2, "closing":1}
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

/** GET /jobs/{id}/similar */
@Serializable
data class JobListResponse(
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

/** GET /jobs/search */
@Serializable
data class JobsSearchResponse(
    val query: String,
    val totalEstimate: Int,
    val companies: List<CompanyDto> = emptyList(),
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

/** GET /onboarding/categories */
@Serializable
data class JobCategoryDto(val code: String, val label: String)

@Serializable
data class CategoriesResponse(val categories: List<JobCategoryDto>)

/** GET /onboarding/popular-companies */
@Serializable
data class PopularCompaniesResponse(val companies: List<CompanyDto>)

/** GET /companies/{id} */
@Serializable
data class CompanyDetailResponse(
    val company: CompanyDto,
    val region: String,                       // "서울/수원" 등 사람 친화 표기
    val about: String,
    val stats: CompanyStats,
    val postings: List<JobDto>,               // 진행중인 공고
    val history: List<JobHistoryDto>,         // 마감된 최근 공고 (공고 없을 때 노출)
)

@Serializable
data class CompanyStats(
    val thisYearCount: Int,
    val avgCloseLabel: String,                // "3주"
    val passRateLabel: String,                // "4%"
)

@Serializable
data class JobHistoryDto(
    val role: String,
    val period: String,                       // "5/1 ~ 5/14 마감"
)

/** GET /users/me/favorites */
@Serializable
data class FavoritesResponse(
    val companies: List<FavoriteCompanyDto>,
)

/** Favorites 목록용. activeJobCount + hasAlarm 추가 표시. */
@Serializable
data class FavoriteCompanyDto(
    val company: CompanyDto,
    val newCount: Int,                        // 오늘 새 공고 N건 (badge)
    val hasAlarm: Boolean = true,
)

/** POST/DELETE 관심기업 토글 응답. */
@Serializable
data class FavoriteToggleResponse(
    val favorited: Boolean,
    val companyId: Int,
)

/** POST /devices/register — FCM 토큰 + 관심직군을 기기ID와 함께 등록(개인화 다이제스트 근거). */
@Serializable
data class DeviceRegisterRequest(
    val fcmToken: String,
    val platform: String = "android",
    val deviceId: String,
    val preferences: DevicePreferences,
)

@Serializable
data class DevicePreferences(
    val categories: List<String> = emptyList(),
)

@Serializable
data class DeviceRegisterResponse(
    val deviceId: String,
    val registeredAt: String,
)

/** GET /notifications/history */
@Serializable
data class NotificationsResponse(
    val notifications: List<NotificationDto>,
    val nextCursor: String? = null,
)

@Serializable
data class NotificationDto(
    val id: String,
    val sentAt: String,                       // ISO8601
    val kind: String,                         // "morning_digest" | "evening_digest" | "deadline"
    val title: String,
    val body: String,
    val jobIds: List<String> = emptyList(),
    val read: Boolean = false,
)

@Serializable
data class NotificationReadResponse(
    val id: String,
    val read: Boolean,
)

/** GET /jobs/upcoming (캘린더용) */
@Serializable
data class UpcomingResponse(
    val days: Int,
    val byDate: Map<String, List<JobDto>>,    // "2026-05-28" → [...]
)
