package com.jobalert.app.data.api

import retrofit2.http.GET
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
        @Query("limit") limit: Int = 30,
    ): JobsTodayResponse

    @GET("api/v1/jobs/{id}")
    suspend fun jobDetail(@Path("id") id: String): JobDetailDto

    @GET("api/v1/jobs/search")
    suspend fun jobsSearch(
        @Query("q") query: String,
        @Query("kind") kind: String? = null,
        @Query("limit") limit: Int = 20,
    ): JobsSearchResponse

    @GET("api/v1/jobs/upcoming")
    suspend fun upcoming(@Query("days") days: Int = 14): UpcomingResponse
}
