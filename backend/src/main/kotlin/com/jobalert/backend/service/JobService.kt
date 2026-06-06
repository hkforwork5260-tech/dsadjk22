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
import com.jobalert.backend.repository.DeviceCategoryRepository
import com.jobalert.backend.repository.JobRepository
import com.jobalert.backend.repository.SavedJobRepository
import com.jobalert.backend.repository.UserFavoriteRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.UUID

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
    private val userFavoriteRepository: UserFavoriteRepository,
    private val deviceCategoryRepository: DeviceCategoryRepository,
    private val savedJobRepository: SavedJobRepository,
    private val mapper: JobMapper,
    private val clock: Clock,
) {
    private val kst = ZoneId.of("Asia/Seoul")

    fun today(
        kind: String?,
        categories: List<String>,
        experiences: List<String>,
        sizes: List<String>,
        limit: Int,
        deviceId: UUID? = null,
    ): JobsTodayResponse {
        val cats = categories.toSet()
        val exps = experiences.toSet()
        val szs = sizes.toSet()
        val hasFilter = cats.isNotEmpty() || exps.isNotEmpty() || szs.isNotEmpty()

        // 회사 다양성(interleave)·개인화·규모 필터를 위해 전체 활성 공고를 후보로 둔다(v0.1 ~1.3천건).
        // 1000 cap이면 최신순에서 밀린 소스(greenhouse 등)가 규모 필터에서 누락 → 넉넉히.
        val pool = PageRequest.of(0, maxOf(limit, 3000))
        var jobs = if (kind == null) {
            jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(pool)
        } else {
            jobRepository.findAllByKindAndIsActiveTrue(kind, pool)
        }

        // 회사 정보: 규모 필터·회사 다양성에 모두 필요하므로 한 번에 로드(N+1 회피).
        val companies = loadCompanies(jobs)

        if (hasFilter) {
            jobs = jobs.filter { job ->
                (cats.isEmpty() || job.jobCategoryCodes?.any { it in cats } == true) &&
                    (exps.isEmpty() || job.experience in exps) &&
                    (szs.isEmpty() || companies[job.companyId]?.size in szs)
            }
        }

        // 개인화 신호(기기 기준). 헤더 없으면 빈 집합 → 가점 없이 회사 다양성(interleave)만 적용.
        val myCategories = deviceId?.let { dev ->
            deviceCategoryRepository.findAllByDeviceId(dev).map { it.categoryCode }.toSet()
        } ?: emptySet()
        val myCompanies = deviceId?.let { dev ->
            userFavoriteRepository.findAllByDeviceId(dev).map { it.companyId }.toSet()
        } ?: emptySet()

        val ranked = rankFeed(jobs, myCategories, myCompanies, limit)

        val counts = JobKindCounts(
            new = jobRepository.countByKindAndIsActiveTrue("NEW").toInt(),
            update = jobRepository.countByKindAndIsActiveTrue("UPDATE").toInt(),
            closing = jobRepository.countByKindAndIsActiveTrue("CLOSING").toInt(),
        )
        return JobsTodayResponse(
            date = OffsetDateTime.now(clock).atZoneSameInstant(kst).toLocalDate().toString(),
            counts = counts,
            jobs = toDtos(ranked),
            nextCursor = null,
        )
    }

    /**
     * 찾아보기 피드 랭킹.
     *
     * 1) 개인화 가점: 관심기업 공고 +2, 관심직군 매칭 공고 +1 (헤더 없으면 모두 0점).
     * 2) 회사 라운드로빈 interleave: 같은 회사가 연달아 나오지 않도록 회사별로 한 건씩 번갈아 뽑는다.
     *    쿠팡처럼 공고가 많은 회사가 피드 앞을 독식하던 문제를 직접 해소한다.
     * 그룹(회사) 순서는 그룹 내 최고 가점 desc → 개인화 신호가 있으면 관심 회사·직군이 앞으로 온다.
     */
    private fun rankFeed(
        jobs: List<Job>,
        myCategories: Set<String>,
        myCompanies: Set<Long>,
        limit: Int,
    ): List<Job> {
        if (jobs.isEmpty()) return emptyList()

        fun score(job: Job): Int {
            var s = 0
            if (job.companyId in myCompanies) s += 2
            if (myCategories.isNotEmpty() && job.jobCategoryCodes?.any { it in myCategories } == true) s += 1
            return s
        }

        // 회사별 그룹(삽입순=firstSeenAt desc 유지) → 그룹 내부 가점 desc(안정정렬) → 그룹을 대표 가점 desc로.
        val groups = jobs.groupBy { it.companyId }
            .values
            .map { list -> list.sortedByDescending { score(it) } }
            .sortedByDescending { group -> group.maxOf { score(it) } }
        val queues = groups.map { ArrayDeque(it) }

        val result = ArrayList<Job>(minOf(limit, jobs.size))
        while (result.size < limit) {
            var progressed = false
            for (q in queues) {
                val next = q.removeFirstOrNull() ?: continue
                result.add(next)
                progressed = true
                if (result.size >= limit) break
            }
            if (!progressed) break
        }
        return result
    }

    fun detail(id: String, deviceId: UUID? = null): JobDetailDto {
        val job = jobRepository.findById(id).orElseThrow {
            NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        }
        val company = companyRepository.findById(job.companyId).orElse(null)
        val saved = deviceId != null && savedJobRepository.existsByDeviceIdAndJobId(deviceId, id)
        val favorited = deviceId != null && userFavoriteRepository.existsByDeviceIdAndCompanyId(deviceId, job.companyId)
        return mapper.toDetailDto(job, company, isFavorited = favorited, isSaved = saved)
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

    /**
     * 검색 + 직군 필터 통합. v0.1 규모(<2천)라 후보를 넓게 가져와 메모리로 거른다.
     *
     * - 직군(categories)만 있고 검색어 없음 → "직군별 둘러보기"(해당 직군 공고).
     * - 검색어(q)는 공백으로 토큰 분해 후, 각 토큰이 제목 또는 회사명에 하나라도 포함되면 매칭(OR).
     *   → "백엔드 개발자"처럼 여러 단어/관련어로도 잡힌다. (오타 교정은 미지원 — 형태소/유사도 필요, v0.2)
     * - 직군+검색어 둘 다면 교집합.
     */
    fun search(q: String, kind: String?, categories: List<String>, limit: Int): JobSearchResponse {
        val cats = categories.toSet()
        val pool = jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(PageRequest.of(0, 2000))
        val companies = loadCompanies(pool)
        val tokens = q.trim().lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }

        var hits = pool.filter { job ->
            val catOk = cats.isEmpty() || job.jobCategoryCodes?.any { it in cats } == true
            val kwOk = tokens.isEmpty() || run {
                val hay = (job.title + " " + (companies[job.companyId]?.name ?: "")).lowercase()
                tokens.any { hay.contains(it) }
            }
            catOk && kwOk
        }
        if (kind != null) hits = hits.filter { it.kind == kind }
        hits = hits.take(limit)

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
