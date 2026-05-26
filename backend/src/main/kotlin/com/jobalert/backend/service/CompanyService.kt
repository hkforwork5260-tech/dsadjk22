package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyStatsDto
import com.jobalert.backend.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
class CompanyService(
    private val mock: MockDataProvider,
) {
    fun detail(id: Long): CompanyDetailDto {
        val c = mock.companies.firstOrNull { it.id == id }
            ?: throw NotFoundException("COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다.")
        return CompanyDetailDto(
            id = c.id,
            name = c.name,
            nameNormalized = c.nameNormalized,
            logoUrl = c.logoUrl,
            industry = c.industry,
            group = c.group,
            size = c.size,
            homepageUrl = c.homepageUrl,
            careersUrl = c.careersUrl,
            description = "${c.name}의 채용 페이지입니다. (mock)",
            activeJobCount = c.activeJobCount,
            isFavorited = false,
            stats = CompanyStatsDto(totalPostings30d = 32, avgPostingsPerWeek = 7.0),
        )
    }

    fun jobs(id: Long, kind: String?, limit: Int): CompanyJobsResponse {
        val company = mock.companies.firstOrNull { it.id == id }
            ?: throw NotFoundException("COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다.")
        val jobs = mock.jobs
            .filter { it.company.id == id && (kind == null || it.kind == kind) }
            .take(limit)
        return CompanyJobsResponse(company = company, jobs = jobs)
    }
}
