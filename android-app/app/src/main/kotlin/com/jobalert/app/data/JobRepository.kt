package com.jobalert.app.data

import com.jobalert.app.data.api.ApiClient
import com.jobalert.app.data.api.ApiService
import com.jobalert.app.data.api.JobDetailDto
import com.jobalert.app.data.api.JobDto
import com.jobalert.app.data.api.CompanyDetailResponse
import com.jobalert.app.data.api.DeviceId
import com.jobalert.app.data.api.DevicePreferencesUpdate
import com.jobalert.app.data.api.FavoritesResponse
import com.jobalert.app.data.api.JobsSearchResponse
import com.jobalert.app.data.api.NotificationsResponse
import com.jobalert.app.data.api.PopularCompaniesResponse
import com.jobalert.app.data.api.UpcomingResponse
import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.theme.JobKind
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * 공고 데이터 접근 한 곳. 백엔드 DTO를 화면이 쓰는 도메인 [Job] 모델로 변환한다.
 *
 * 화면(Compose)은 DTO를 직접 모른다 — Repository가 경계. 백엔드 응답 형태가 바뀌어도
 * 매핑만 고치면 화면은 그대로다.
 */
class JobRepository(
    private val api: ApiService = ApiClient.api,
) {
    /** /jobs/today — 오늘 피드(전체 kind) + kind별 카운트. 화면이 섹션 토글로 로컬 필터.
     *  categories 지정 시 해당 직군만(서버 필터). */
    suspend fun todayFeed(
        categories: List<String> = emptyList(),
        experiences: List<String> = emptyList(),
        sizes: List<String> = emptyList(),
    ): TodayFeed {
        val res = api.jobsToday(
            kind = null, categories = categories, experiences = experiences, sizes = sizes, limit = 50,
        )
        return TodayFeed(
            counts = res.counts.entries.associate { (k, v) -> kindOf(k) to v }
                .filterKeys { it != null }
                .mapKeys { it.key!! },
            jobs = res.jobs.map { it.toDomain() },
        )
    }

    data class TodayFeed(
        val counts: Map<JobKind, Int>,
        val jobs: List<Job>,
    )

    /** /jobs/search — 검색 결과. 화면이 DTO 그대로 쓰므로 응답을 그대로 반환.
     *  (백엔드는 현재 제목 검색만 → companies는 빈 리스트로 옴) */
    suspend fun search(query: String): JobsSearchResponse = api.jobsSearch(query)

    /** /jobs/upcoming — 마감 임박(캘린더용). days일 내 마감 공고를 날짜별로. */
    suspend fun upcoming(days: Int = 40): UpcomingResponse = api.upcoming(days)

    /** /companies/{id}/page — 회사 상세(회사·지역·통계·진행공고·이력). */
    suspend fun companyDetail(id: Int): CompanyDetailResponse = api.companyPage(id)

    /** 온보딩 추천 회사(공고 많은 순). */
    suspend fun popularCompanies(): PopularCompaniesResponse = api.popularCompanies()

    /** 관심기업 목록 (현재 기기 기준). */
    suspend fun favorites(): FavoritesResponse = api.favorites()

    /** 관심기업 추가/삭제. */
    suspend fun addFavorite(companyId: Int) = api.addFavorite(companyId)
    suspend fun removeFavorite(companyId: Int) = api.removeFavorite(companyId)

    /** 알림 히스토리 (현재 기기 기준 다이제스트 기록). */
    suspend fun notifications(): NotificationsResponse = api.notifications()

    /** 알림 읽음 처리. */
    suspend fun markNotificationRead(id: String) = api.markNotificationRead(id)

    /** 아침/저녁 푸시 on/off 갱신(이 기기). */
    suspend fun updatePushPreferences(morning: Boolean, evening: Boolean) =
        api.updatePreferences(DeviceId.value, DevicePreferencesUpdate(pushMorning = morning, pushEvening = evening))

    /** /jobs/{id} — 공고 상세. 도메인 Job + 상세 텍스트(요약·설명·원본URL). */
    suspend fun jobDetail(id: String): Job = api.jobDetail(id).toDomain()

    /** /jobs/{id}/similar — 비슷한 공고(같은 업종). */
    suspend fun similar(id: String): List<Job> = api.similar(id).jobs.map { it.toDomain() }

    private val kst = ZoneId.of("Asia/Seoul")

    private fun JobDetailDto.toDomain(): Job = Job(
        id = id,
        company = company.name,
        companyId = company.id,
        logo = company.logo,
        role = title,
        kind = kindOf(kind) ?: JobKind.NEW,
        dday = dday,
        dateText = deadline?.let { formatDeadline(it) } ?: "상시",
        location = location,
        experience = experience,
        education = education,
        summary = summary,            // 꽁이 한줄요약(없으면 빈 문자열)
        tags = tags,
        originalUrl = originalUrl,     // 지원하기 → 원본 채용 URL
    )

    private fun JobDto.toDomain(): Job = Job(
        id = id,
        company = company.name,
        logo = company.logo,
        role = title,
        kind = kindOf(kind) ?: JobKind.NEW,
        dday = dday,
        dateText = deadline?.let { formatDeadline(it) } ?: "상시",
        location = location,
        experience = experience,
        education = education,
        tags = tags,
    )

    /** "NEW"/"new" → JobKind.NEW. 모르는 값(EXPIRED 등)은 null. */
    private fun kindOf(raw: String): JobKind? =
        runCatching { JobKind.valueOf(raw.uppercase()) }.getOrNull()

    /** ISO8601 마감일시 → "~6/15" (KST 기준). 파싱 실패 시 빈 문자열. */
    private fun formatDeadline(iso: String): String = runCatching {
        val d = OffsetDateTime.parse(iso).atZoneSameInstant(kst).toLocalDate()
        "~${d.monthValue}/${d.dayOfMonth}"
    }.getOrDefault("")
}
