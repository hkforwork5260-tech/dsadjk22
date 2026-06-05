package com.jobalert.backend.service

import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobDto
import com.jobalert.backend.dto.JobKindCounts
import com.jobalert.backend.dto.JobListResponse
import com.jobalert.backend.dto.JobSearchResponse
import com.jobalert.backend.dto.JobUpcomingResponse
import com.jobalert.backend.dto.JobsTodayResponse
import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.Job
import com.jobalert.backend.exception.NotFoundException
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * 공고 조회 서비스. Phase 3에서 mock → 실제 DB(JobRepository)로 교체.
 *
 * 회사 임베드는 공고마다 따로 조회하면 N+1이 되므로, 목록은 companyId 모아서 한 번에 로드한다.
 */
@Service
@Transactional(readOnly = true)
class JobService(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val mapper: JobMapper,
    private val clock: Clock,
) {
    private val kst = ZoneId.of("Asia/Seoul")

    fun today(kind: String?, categories: List<String>, limit: Int): JobsTodayResponse {
        val page = PageRequest.of(0, limit)
        val jobs = if (kind == null) {
            jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(page)
        } else {
            jobRepository.findAllByKindAndIsActiveTrue(kind, page)
        }
        val counts = JobKindCounts(
            new = jobRepository.countByKindAndIsActiveTrue("NEW").toInt(),
            update = jobRepository.countByKindAndIsActiveTrue("UPDATE").toInt(),
            closing = jobRepository.countByKindAndIsActiveTrue("CLOSING").toInt(),
        )
        return JobsTodayResponse(
            date = OffsetDateTime.now(clock).atZoneSameInstant(kst).toLocalDate().toString(),
            counts = counts,
            jobs = toDtos(jobs),
            nextCursor = null,
        )
    }

    fun detail(id: String): JobDetailDto {
        val job = jobRepository.findById(id).orElseThrow {
            NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        }
        val company = companyRepository.findById(job.companyId).orElse(null)
        return mapper.toDetailDto(job, company)
    }

    fun similar(id: String): JobListResponse {
        val base = jobRepository.findById(id).orElseThrow {
            NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        }
        val industry = companyRepository.findById(base.companyId).orElse(null)?.industry
            ?: return JobListResponse(jobs = emptyList())
        val jobs = jobRepository.findSimilarByIndustry(industry, id, PageRequest.of(0, 10))
        return JobListResponse(jobs = toDtos(jobs))
    }

    fun search(q: String, kind: String?, limit: Int): JobSearchResponse {
        // 현재는 제목 LIKE 검색. 회사명·태그 검색은 인덱스/풀텍스트 도입 시 확장.
        var hits = jobRepository.searchByKeyword(q, PageRequest.of(0, limit))
        if (kind != null) hits = hits.filter { it.kind == kind }
        return JobSearchResponse(
            query = q,
            totalEstimate = hits.size,
            jobs = toDtos(hits),
            nextCursor = null,
        )
    }

    fun upcoming(days: Int): JobUpcomingResponse {
        val now = OffsetDateTime.now(clock)
        val jobs = jobRepository.findUpcoming(now, now.plusDays(days.toLong()))
        val dtos = toDtos(jobs)
        val byDate = dtos
            .filter { it.deadline != null }
            .groupBy { it.deadline!!.atZoneSameInstant(kst).toLocalDate().toString() }
            .toSortedMap()
        return JobUpcomingResponse(days = days, byDate = byDate)
    }

    /** 공고 목록을 DTO로. 회사를 companyId 묶음으로 한 번에 로드해 N+1 회피. */
    private fun toDtos(jobs: List<Job>): List<JobDto> {
        if (jobs.isEmpty()) return emptyList()
        val companies = loadCompanies(jobs)
        return jobs.map { mapper.toDto(it, companies[it.companyId]) }
    }

    private fun loadCompanies(jobs: List<Job>): Map<Long, Company> {
        val ids = jobs.map { it.companyId }.toSet()
        return companyRepository.findAllById(ids).associateBy { it.id!! }
    }
}
