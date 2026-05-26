package com.jobalert.backend.dto

import java.time.OffsetDateTime

data class CompanyEmbedDto(
    val id: Long,
    val name: String,
    val logo: String,
    val logoUrl: String?,
    val industry: String?,
)

data class JobDto(
    val id: String,
    val company: CompanyEmbedDto,
    val title: String,
    val kind: String,
    val dday: String,
    val deadline: OffsetDateTime?,
    val location: String?,
    val experience: String?,
    val education: String?,
    val tags: List<String>,
    val isFavorited: Boolean = false,
)

data class JobDetailDto(
    val id: String,
    val company: CompanyEmbedDto,
    val title: String,
    val kind: String,
    val dday: String,
    val deadline: OffsetDateTime?,
    val postingDate: OffsetDateTime?,
    val location: String?,
    val experience: String?,
    val education: String?,
    val salary: String?,
    val jobCategories: List<String>,
    val tags: List<String>,
    val description: String?,
    val summary: String?,
    val preferred: List<String>,
    val process: List<String>,
    val originalUrl: String?,
    val source: String,
    val isFavorited: Boolean = false,
)

data class JobsTodayResponse(
    val date: String,
    val counts: JobKindCounts,
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

data class JobKindCounts(
    val new: Int = 0,
    val update: Int = 0,
    val closing: Int = 0,
)

data class JobSearchResponse(
    val query: String,
    val totalEstimate: Int,
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

data class JobListResponse(
    val jobs: List<JobDto>,
    val nextCursor: String? = null,
)

data class JobUpcomingResponse(
    val days: Int,
    val byDate: Map<String, List<JobDto>>,
)
