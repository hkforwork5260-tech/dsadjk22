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
import com.jobalert.app.data.model.JobCategories
import com.jobalert.app.data.model.JobCategoryCodes
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
        deadlineDays: Int = -1,
    ): TodayFeed {
        val res = api.jobsToday(
            // 카운트(예: 945) 대비 다 보이게 넉넉히(사실상 전부). 메인·찾아보기 공용.
            kind = null, categories = categories, experiences = experiences, sizes = sizes, limit = 1000,
            deadlineDays = deadlineDays.takeIf { it >= 0 },   // -1=전체 → null
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

    /** /jobs/search — 검색(단어분해 제목·회사명) + 직군 필터(categories). 화면이 DTO 그대로 사용. */
    suspend fun search(query: String, categories: List<String> = emptyList()): JobsSearchResponse =
        api.jobsSearch(query = query, categories = categories)

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

    /** 저장한 공고 목록(현재 기기, 최신순). */
    suspend fun savedJobs(): List<Job> = api.savedJobs().jobs.map { it.toDomain() }

    /** 공고 저장/해제. 호출 성공 여부 반환(실패 시 화면이 롤백·안내). */
    suspend fun saveJob(id: String): Boolean = runCatching { api.addSavedJob(id); true }.getOrDefault(false)
    suspend fun unsaveJob(id: String): Boolean = runCatching { api.removeSavedJob(id); true }.getOrDefault(false)

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

    /** 본 공고 목록 — 로컬 SeenJobs ID들로 공고 조회. 빈 목록이면 호출 생략. */
    suspend fun jobsByIds(ids: List<String>): List<Job> =
        if (ids.isEmpty()) emptyList() else api.jobsByIds(ids).jobs.map { it.toDomain() }

    /** 찾아보기 피드 — 인스타 탐색 랭킹(관심·다양성·발견). '오늘' 필터와 무관. */
    suspend fun discoverFeed(): List<Job> = api.discover().jobs.map { it.toDomain() }

    private val kst = ZoneId.of("Asia/Seoul")

    // 공고 제목 끝의 상투어("~채용합니다/모집합니다/모집/채용/공고" 등) 제거용.
    private val titleTail = Regex("\\s*(을|를)?\\s*(직원\\s*)?(채용\\s*공고|모집\\s*공고|채용합니다|모집합니다|모집중|채용중|구인합니다|채용|모집|구인|공고)\\s*\\.?\\s*$")

    /**
     * 표시용 제목 통일: 끝의 상투어 제거 + 신입/인턴이면 앞에 "(신입)"·"(인턴)" 접두.
     * 예: "여행사 영업기획 사무원 채용합니다." → "여행사 영업기획 사무원" / 신입이면 "(신입) ..."
     */
    private fun displayRole(rawTitle: String, experience: String): String {
        val cleaned = rawTitle.trim().replace(titleTail, "").trim().removeSuffix(".").trim()
        val t = cleaned.ifBlank { rawTitle.trim() }
        val exp = when {
            experience.contains("신입") -> "신입"
            experience.contains("인턴") -> "인턴"
            else -> null
        }
        return if (exp != null) "($exp) $t" else t
    }

    private fun JobDetailDto.toDomain(): Job = Job(
        id = id,
        company = company.name,
        companyId = company.id,
        logo = company.logo,
        role = displayRole(title, experience),
        kind = kindOf(kind) ?: JobKind.NEW,
        dday = dday,
        dateText = deadline?.let { formatDeadline(it) } ?: "상시",
        location = location,
        experience = experience,
        education = education,
        salary = salary,
        tags = tags,
        categories = categoryLabels(jobCategories),
        companySize = company.size,
        description = description,
        originalUrl = originalUrl,     // 지원하기 → 원본 채용 URL
        isSaved = isSaved,
        isFavoriteCompany = isFavorited,
    )

    private fun JobDto.toDomain(): Job = Job(
        id = id,
        company = company.name,
        companyId = company.id,
        logo = company.logo,
        role = displayRole(title, experience),
        kind = kindOf(kind) ?: JobKind.NEW,
        dday = dday,
        dateText = deadline?.let { formatDeadline(it) } ?: "상시",
        location = location,
        experience = experience,
        education = education,
        salary = salary,
        tags = tags,
        categories = categoryLabels(jobCategories),
        companySize = company.size,
        description = description,
    )

    /** 백엔드 직군 코드(it_dev_data 등) → 한글 라벨(IT개발·데이터). 모르는 코드는 제외. */
    private fun categoryLabels(codes: List<String>): List<String> =
        codes.mapNotNull { code ->
            val i = JobCategoryCodes.indexOf(code)
            if (i >= 0) JobCategories[i] else null
        }

    /** "NEW"/"new" → JobKind.NEW. 모르는 값(EXPIRED 등)은 null. */
    private fun kindOf(raw: String): JobKind? =
        runCatching { JobKind.valueOf(raw.uppercase()) }.getOrNull()

    /** ISO8601 마감일시 → "~6/15" (KST 기준). 파싱 실패 시 빈 문자열. */
    private fun formatDeadline(iso: String): String = runCatching {
        val d = OffsetDateTime.parse(iso).atZoneSameInstant(kst).toLocalDate()
        "~${d.monthValue}/${d.dayOfMonth}"
    }.getOrDefault("")
}
