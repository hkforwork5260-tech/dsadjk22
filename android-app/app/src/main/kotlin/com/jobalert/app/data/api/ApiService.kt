package com.jobalert.app.data.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 백엔드 REST 인터페이스. Retrofit이 이 인터페이스 구현체를 만들어 준다.
 *
 * 지금은 **응답 모양이 백엔드와 일치 검증된 jobs 엔드포인트만** 둔다.
 * companies·onboarding·notifications는 백엔드가 아직 mock이거나 응답 형태가 FE DTO와
 * 달라서, 붙이면 역직렬화에서 깨진다 → 백엔드 실 DB화 후 추가(별도 태스크).
 */
interface ApiService {

    @GET("api/v1/jobs/today")
    suspend fun jobsToday(
        @Query("kind") kind: String? = null,
        @Query("categories") categories: List<String> = emptyList(),
        @Query("experiences") experiences: List<String> = emptyList(),
        @Query("sizes") sizes: List<String> = emptyList(),
        @Query("limit") limit: Int = 30,
    ): JobsTodayResponse

    @GET("api/v1/jobs/{id}")
    suspend fun jobDetail(@Path("id") id: String): JobDetailDto

    @GET("api/v1/jobs/{id}/similar")
    suspend fun similar(@Path("id") id: String): JobListResponse

    @GET("api/v1/jobs/search")
    suspend fun jobsSearch(
        @Query("q") query: String,
        @Query("kind") kind: String? = null,
        @Query("limit") limit: Int = 20,
    ): JobsSearchResponse

    @GET("api/v1/jobs/upcoming")
    suspend fun upcoming(@Query("days") days: Int = 14): UpcomingResponse

    @GET("api/v1/companies/{id}/page")
    suspend fun companyPage(@Path("id") id: Int): CompanyDetailResponse

    @GET("api/v1/onboarding/popular-companies")
    suspend fun popularCompanies(): PopularCompaniesResponse

    // 관심기업 — 기기ID는 ApiClient 인터셉터가 X-Device-Id 헤더로 자동 첨부.
    @GET("api/v1/users/me/favorites")
    suspend fun favorites(): FavoritesResponse

    @POST("api/v1/users/me/favorites/{companyId}")
    suspend fun addFavorite(@Path("companyId") companyId: Int): FavoriteToggleResponse

    @DELETE("api/v1/users/me/favorites/{companyId}")
    suspend fun removeFavorite(@Path("companyId") companyId: Int): FavoriteToggleResponse

    // 저장한 공고(북마크) — 기기ID는 ApiClient 인터셉터가 X-Device-Id 헤더로 자동 첨부.
    @GET("api/v1/users/me/saved")
    suspend fun savedJobs(): SavedJobsResponse

    @POST("api/v1/users/me/saved/{jobId}")
    suspend fun addSavedJob(@Path("jobId") jobId: String): SaveToggleResponse

    @DELETE("api/v1/users/me/saved/{jobId}")
    suspend fun removeSavedJob(@Path("jobId") jobId: String): SaveToggleResponse

    @GET("api/v1/notifications/history")
    suspend fun notifications(@Query("limit") limit: Int = 30): NotificationsResponse

    @POST("api/v1/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): NotificationReadResponse

    @POST("api/v1/devices/register")
    suspend fun registerDevice(@Body req: DeviceRegisterRequest): DeviceRegisterResponse

    @PATCH("api/v1/devices/{deviceId}/preferences")
    suspend fun updatePreferences(
        @Path("deviceId") deviceId: String,
        @Body req: DevicePreferencesUpdate,
    ): DevicePreferences
}
