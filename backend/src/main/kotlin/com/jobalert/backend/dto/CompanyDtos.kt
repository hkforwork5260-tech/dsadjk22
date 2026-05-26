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
