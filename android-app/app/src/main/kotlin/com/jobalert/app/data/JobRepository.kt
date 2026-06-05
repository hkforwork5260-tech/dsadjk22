package com.jobalert.app.data

import com.jobalert.app.data.api.ApiClient
import com.jobalert.app.data.api.ApiService
import com.jobalert.app.data.api.JobDto
import com.jobalert.app.data.api.CompanyDetailResponse
import com.jobalert.app.data.api.JobsSearchResponse
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
    /** /jobs/today — 오늘 피드(전체 kind) + kind별 카운트. 화면이 섹션 토글로 로컬 필터. */
    suspend fun todayFeed(): TodayFeed {
        val res = api.jobsToday(kind = null, limit = 50)
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

    private val kst = ZoneId.of("Asia/Seoul")

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
