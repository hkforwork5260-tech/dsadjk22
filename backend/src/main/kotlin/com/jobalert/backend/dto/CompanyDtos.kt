package com.jobalert.backend.dto

data class CompanyDto(
    val id: Long,
    val name: String,
    val nameNormalized: String,
    val logoUrl: String?,
    val industry: String?,
    val group: String?,
    val size: String?,
    val homepageUrl: String?,
    val careersUrl: String?,
    val activeJobCount: Int = 0,
    val isFavorited: Boolean = false,
)

data class CompanyStatsDto(
    val totalPostings30d: Int,
    val avgPostingsPerWeek: Double,
)

data class CompanyDetailDto(
    val id: Long,
    val name: String,
    val nameNormalized: String,
    val logoUrl: String?,
    val industry: String?,
    val group: String?,
    val size: String?,
    val homepageUrl: String?,
    val careersUrl: String?,
    val description: String?,
    val activeJobCount: Int,
    val isFavorited: Boolean,
    val stats: CompanyStatsDto,
)

data class CompanyJobsResponse(
    val company: CompanyDto,
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

data class CompanyListResponse(
    val companies: List<CompanyDto>,
)

// ── 회사 상세 페이지 (안드로이드 CompanyDetailScreen 응답 모양) ──

data class CompanyPageResponse(
    val company: CompanyPageCompany,
    val region: String,
    val about: String,
    val stats: CompanyPageStats,
    val postings: List<JobDto>,          // 진행중 공고
    val history: List<JobHistoryItem>,   // 마감된 최근 공고 (공고 없을 때 노출)
)

data class CompanyPageCompany(
    val id: Long,
    val name: String,
    val logo: String,            // 로고용 짧은 텍스트
    val logoUrl: String?,
    val industry: String?,
    val size: String?,
    val isFavorited: Boolean = false,
)

data class CompanyPageStats(
    val thisYearCount: Int,
    val avgCloseLabel: String,   // "—"(미산출) 또는 "3주"
    val passRateLabel: String,   // "—"(데이터 없음)
)

data class JobHistoryItem(
    val role: String,
    val period: String,          // "~6/15 마감" 등
)
