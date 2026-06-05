package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyPageCompany
import com.jobalert.backend.dto.CompanyPageResponse
import com.jobalert.backend.dto.CompanyPageStats
import com.jobalert.backend.dto.CompanyStatsDto
import com.jobalert.backend.dto.JobHistoryItem
import com.jobalert.backend.entity.Job
import com.jobalert.backend.exception.NotFoundException
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import com.jobalert.backend.repository.UserFavoriteRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneId
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CompanyService(
    private val companyRepository: CompanyRepository,
    private val jobRepository: JobRepository,
    private val favoriteRepository: UserFavoriteRepository,
    private val mapper: JobMapper,
) {
    private val kst = ZoneId.of("Asia/Seoul")
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

    /** 회사 상세 페이지 — 안드로이드 CompanyDetailScreen 응답 모양으로 실데이터 조립. */
    fun page(id: Long, deviceId: UUID? = null): CompanyPageResponse {
        val c = companyRepository.findById(id).orElseThrow {
            NotFoundException("COMPANY_NOT_FOUND", "회사를 찾을 수 없습니다.")
        }
        val favorited = deviceId != null && favoriteRepository.existsByDeviceIdAndCompanyId(deviceId, id)
        val active = jobRepository.findAllByCompanyIdAndIsActiveTrueOrderByFirstSeenAtDesc(id, PageRequest.of(0, 50))
        val closed = jobRepository.findAllByCompanyIdAndIsActiveFalseOrderByClosedAtDesc(id, PageRequest.of(0, 10))

        // 지역: 진행중 공고 근무지 중 최빈값. 없으면 "—".
        val region = active.mapNotNull { it.location?.takeIf { l -> l.isNotBlank() } }
            .groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: "—"

        return CompanyPageResponse(
            company = CompanyPageCompany(
                id = c.id!!,
                name = c.name,
                logo = c.name.take(2),
                logoUrl = c.logoUrl,
                industry = c.industry,
                size = c.size,
                isFavorited = favorited,
            ),
            region = region,
            about = c.description ?: "${c.name} 채용 정보입니다. 자세한 내용은 각 공고 원문을 확인하세요.",
            stats = CompanyPageStats(
                // "올해 신규" 근사값 = 현재 진행중 공고 수(전부 올해 수집분). 정밀 집계는 후속.
                thisYearCount = active.size,
                avgCloseLabel = "—",   // 평균 마감기간 미산출
                passRateLabel = "—",   // 합격률 데이터 없음
            ),
            postings = active.map { mapper.toDto(it, c) },
            history = closed.map { JobHistoryItem(role = it.title, period = periodLabel(it)) },
        )
    }

    private fun periodLabel(job: Job): String {
        val d = job.deadline?.atZoneSameInstant(kst)?.toLocalDate() ?: return "마감"
        return "~${d.monthValue}/${d.dayOfMonth} 마감"
    }
}
