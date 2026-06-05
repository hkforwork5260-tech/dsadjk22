package com.jobalert.backend.service

import com.jobalert.backend.dto.CategoriesResponse
import com.jobalert.backend.dto.CompanyBriefDto
import com.jobalert.backend.dto.PopularCompaniesResponse
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OnboardingService(
    private val mock: MockDataProvider,
    private val companyRepository: CompanyRepository,
    private val jobRepository: JobRepository,
) {
    /** 직군 21개 — 정적 참조 데이터. */
    fun categories(): CategoriesResponse =
        CategoriesResponse(categories = mock.categories)

    /** 추천 회사 — 진행중 공고가 많은 회사 순(실데이터). 온보딩 스와이프용. */
    fun popularCompanies(categories: List<String>): PopularCompaniesResponse {
        val ids = jobRepository.findTopCompanyIdsByActiveJobs(PageRequest.of(0, 12))
        val byId = companyRepository.findAllById(ids).associateBy { it.id }
        val companies = ids.mapNotNull { byId[it] }.map { c ->
            CompanyBriefDto(
                id = c.id!!,
                name = c.name,
                logo = c.name.take(2),
                logoUrl = c.logoUrl,
                industry = c.industry,
                size = c.size,
                activeJobCount = jobRepository.countByCompanyIdAndIsActiveTrue(c.id!!).toInt(),
                isFavorited = false,
            )
        }
        return PopularCompaniesResponse(companies = companies)
    }
}
