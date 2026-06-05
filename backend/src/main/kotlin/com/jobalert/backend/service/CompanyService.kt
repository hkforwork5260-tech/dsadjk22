package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyStatsDto
import com.jobalert.backend.exception.NotFoundException
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val jobRepository: JobRepository,
    private val mapper: JobMapper,
) {
    fun detail(id: Long): CompanyDetailDto {
        val c = companyRepository.findById(id).orElseThrow {
            NotFoundException("COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다.")
        }
        val activeCount = jobRepository.countByCompanyIdAndIsActiveTrue(id).toInt()
        return CompanyDetailDto(
            id = c.id!!,
            name = c.name,
            nameNormalized = c.nameNormalized,
            logoUrl = c.logoUrl,
            industry = c.industry,
            group = c.groupName,
            size = c.size,
            homepageUrl = c.homepageUrl,
            careersUrl = c.careersUrl,
            description = c.description,
            activeJobCount = activeCount,
            isFavorited = false,
            // 통계는 실데이터 누적 후 산출 — 우선 활성 공고 수 기반 근사. 정식 집계는 후속.
            stats = CompanyStatsDto(totalPostings30d = activeCount, avgPostingsPerWeek = 0.0),
        )
    }

    fun jobs(id: Long, kind: String?, limit: Int): CompanyJobsResponse {
        val c = companyRepository.findById(id).orElseThrow {
            NotFoundException("COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다.")
        }
        val page = PageRequest.of(0, limit)
        val jobs = if (kind == null) {
            jobRepository.findAllByCompanyIdAndIsActiveTrueOrderByFirstSeenAtDesc(id, page)
        } else {
            jobRepository.findAllByCompanyIdAndKindAndIsActiveTrue(id, kind, page)
        }
        val activeCount = jobRepository.countByCompanyIdAndIsActiveTrue(id).toInt()
        return CompanyJobsResponse(
            company = mapper.toCompanyDto(c, activeCount),
            jobs = jobs.map { mapper.toDto(it, c) },
        )
    }
}
