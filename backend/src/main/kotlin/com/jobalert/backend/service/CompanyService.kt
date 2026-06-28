package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyPageCompany
import com.jobalert.backend.dto.CompanyPageResponse
import com.jobalert.backend.dto.CompanyPageStats
import com.jobalert.backend.dto.CompanyStatsDto
import com.jobalert.backend.dto.JobHistoryItem
import com.jobalert.backend.entity.Company
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

    /**
     * 회사명 검색 — 관심기업 추가 화면용. 진행중 공고 있는 회사만, 활성 공고수 많은 순.
     * deviceId 있으면 이미 관심기업인지(isFavorited) 반영.
     */
    fun search(query: String, deviceId: UUID? = null, limit: Int = 20): List<CompanyDto> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        val ql = q.lowercase()
        // 이름 관련도 가중치: 정확일치 > 접두일치 > 부분일치. "삼성"→삼성전자가 르노삼성보다 위로.
        fun nameScore(name: String): Int {
            val n = name.lowercase()
            return when {
                n == ql -> 3
                n.startsWith(ql) -> 2
                else -> 1
            }
        }
        val favoriteIds = deviceId?.let { dev -> favoriteRepository.findAllByDeviceId(dev).map { it.companyId }.toSet() } ?: emptySet()
        return companyRepository.searchByName(q, PageRequest.of(0, 60))
            .map { c ->
                val cnt = jobRepository.countByCompanyIdAndIsActiveTrue(c.id!!).toInt()
                CompanyDto(
                    id = c.id!!,
                    name = c.name,
                    nameNormalized = c.nameNormalized,
                    logoUrl = c.logoUrl,
                    industry = c.industry,
                    group = c.groupName,
                    size = c.size,
                    homepageUrl = c.homepageUrl,
                    careersUrl = c.careersUrl,
                    activeJobCount = cnt,
                    isFavorited = c.id in favoriteIds,
                )
            }
            // 이름 관련도 우선, 동점이면 진행중 공고 많은 순.
            .sortedWith(compareByDescending<CompanyDto> { nameScore(it.name) }.thenByDescending { it.activeJobCount })
            .take(limit)
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

        // 평균 마감기간: 등록~마감 일수 평균(active+closed 중 둘 다 있는 공고). 없으면 "—".
        val spans = (active + closed).mapNotNull { j ->
            val p = j.postingDate; val d = j.deadline
            if (p != null && d != null && d.isAfter(p)) java.time.Duration.between(p, d).toDays() else null
        }
        val avgCloseLabel = if (spans.isEmpty()) "—" else "${(spans.average() / 7).toInt().coerceAtLeast(1)}주"

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
            about = c.description?.takeIf { it.isNotBlank() } ?: buildAbout(c, active.size, region),
            stats = CompanyPageStats(
                // "올해 신규" 근사값 = 현재 진행중 공고 수(전부 올해 수집분). 정밀 집계는 후속.
                thisYearCount = active.size,
                avgCloseLabel = avgCloseLabel,
                passRateLabel = "—",   // 합격률 데이터 없음(지원결과 미수집)
            ),
            postings = active.map { mapper.toDto(it, c) },
            history = closed.map { JobHistoryItem(role = it.title, period = periodLabel(it)) },
        )
    }

    /** 회사 description이 없을 때, 산업·규모·근무지·진행 공고 수로 소개문을 자동 생성. */
    private fun buildAbout(c: Company, activeCount: Int, region: String): String {
        val ind = c.industry?.takeIf { it.isNotBlank() }
        val size = sizeLabelKo(c.size)
        val head = when {
            ind != null && size != null -> "$ind 분야 $size"
            ind != null -> "$ind 분야 기업"
            size != null -> size
            else -> null
        }
        return buildString {
            head?.let { append("$it. ") }
            if (region.isNotBlank() && region != "—") append("주요 근무지는 $region. ")
            append(if (activeCount > 0) "현재 ${activeCount}건의 채용을 진행 중이에요." else "지금은 진행 중인 공고가 없어요.")
        }
    }

    private fun sizeLabelKo(code: String?): String? = when (code) {
        "large_corp" -> "대기업"
        "mid_corp" -> "중견기업"
        "small" -> "중소기업"
        "public" -> "공기업"
        "startup", "startup_unicorn" -> "스타트업"
        "foreign" -> "외국계"
        else -> null
    }

    private fun periodLabel(job: Job): String {
        val d = job.deadline?.atZoneSameInstant(kst)?.toLocalDate() ?: return "마감"
        return "~${d.monthValue}/${d.dayOfMonth} 마감"
    }
}
