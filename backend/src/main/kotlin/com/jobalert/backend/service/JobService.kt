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
        deadlineDays: Int? = null,
    ): JobsTodayResponse {
        val cats = categories.toSet()
        val exps = experiences.toSet()
        val szs = sizes.toSet()
        val hasFilter = cats.isNotEmpty() || exps.isNotEmpty() || szs.isNotEmpty()

        // 회사 다양성(interleave)·개인화·규모 필터를 위해 전체 활성 공고를 후보로 둔다(v0.1 ~1.3천건).
        // 1000 cap이면 최신순에서 밀린 소스(greenhouse 등)가 규모 필터에서 누락 → 넉넉히.
        // 활성 공고(~1.4천) 전부 담되 메모리 과부하(무료 박스 OOM) 줄이려 3000→2000.
        val pool = PageRequest.of(0, maxOf(limit, 2000))
        var jobs = if (kind == null) {
            jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(pool)
        } else {
            jobRepository.findAllByKindAndIsActiveTrue(kind, pool)
        }

        // 마감 지난 공고 제외(상시=null은 유지). 진입 시각(now) 기준이라, 오전엔 열려 있던 게
        // 오후엔 마감됐으면 자동으로 빠진다. (앱이 진입할 때마다 today를 다시 호출)
        val nowOdt = OffsetDateTime.now(clock)
        jobs = jobs.filter { it.deadline == null || !it.deadline!!.isBefore(nowOdt) }

        // 회사 정보: 규모 필터·회사 다양성에 모두 필요하므로 한 번에 로드(N+1 회피).
        val companies = loadCompanies(jobs)

        if (hasFilter) {
            jobs = jobs.filter { job ->
                (cats.isEmpty() || job.jobCategoryCodes?.any { it in cats } == true) &&
                    (exps.isEmpty() || job.experience in exps) &&
                    (szs.isEmpty() || companies[job.companyId]?.size in szs)
            }
        }

        // 마감일 필터: deadlineDays=3이면 "오늘~D-3 이내 마감"만. 상시(마감없음)는 제외.
        if (deadlineDays != null && deadlineDays >= 0) {
            val todayKst = OffsetDateTime.now(clock).atZoneSameInstant(kst).toLocalDate()
            jobs = jobs.filter { job ->
                job.deadline?.let { dl ->
                    java.time.temporal.ChronoUnit.DAYS.between(todayKst, dl.atZoneSameInstant(kst).toLocalDate()) in 0..deadlineDays
                } ?: false
            }
        }

        // 표시용 kind 재계산(메모리, 미저장): 오늘 처음 등장한 공고 = NEW(하루 종일 유지 — 재수집에도
        // 안 사라짐). 마감 7일 이내 = CLOSING(앱에선 '마감임박'). 그 외 변경=UPDATE, 나머지=ACTIVE.
        val todayKstDate = OffsetDateTime.now(clock).atZoneSameInstant(kst).toLocalDate()
        jobs.forEach { it.kind = displayKind(it, todayKstDate) }

        // 개인화 신호(기기 기준). 헤더 없으면 빈 집합 → 가점 없이 회사 다양성(interleave)만 적용.
        val myCategories = deviceId?.let { dev ->
            deviceCategoryRepository.findAllByDeviceId(dev).map { it.categoryCode }.toSet()
        } ?: emptySet()
        val myCompanies = deviceId?.let { dev ->
            userFavoriteRepository.findAllByDeviceId(dev).map { it.companyId }.toSet()
        } ?: emptySet()

        val ranked = rankFeed(jobs, myCategories, myCompanies, limit)

        // 카운트는 (필터 적용된) 현재 후보 jobs에서 센다 → 필터 시 헤더·칩 숫자도 같이 바뀐다.
        // pool(≥3000)이 전체 활성(<2천)을 다 담으므로 jobs엔 매칭 활성 공고가 빠짐없이 있어 정확하다.
        val counts = JobKindCounts(
            new = jobs.count { it.kind == "NEW" },
            update = jobs.count { it.kind == "UPDATE" },
            closing = jobs.count { it.kind == "CLOSING" },
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

    /**
     * 찾아보기 전용 랭킹(인스타 탐색 느낌). '오늘' 필터와 무관하게 전체 활성 공고를 섞어 보여준다.
     * 점수(관심기업+5·관심직군+3·저장취향+2·최신+2/+1·마감임박+1) → 회사 라운드로빈(다양성)
     * → 관심:발견 3:1 머지(발견 약 25%, 필터버블 방지) → 그룹 random tiebreak로 매번 신선.
     * '본 공고' 후순위는 클라이언트가 로컬 SeenJobs로 처리한다.
     */
    fun discover(deviceId: UUID?, limit: Int): JobListResponse {
        val now = OffsetDateTime.now(clock)
        // 마감 지난 공고는 찾아보기에서 제외(상시채용=deadline null은 유지).
        val pool = jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(PageRequest.of(0, 3000))
            .filter { it.deadline == null || !it.deadline!!.isBefore(now) }
        if (pool.isEmpty()) return JobListResponse(jobs = emptyList())

        val myCategories = deviceId?.let { dev ->
            deviceCategoryRepository.findAllByDeviceId(dev).map { it.categoryCode }.toSet()
        } ?: emptySet()
        val myCompanies = deviceId?.let { dev ->
            userFavoriteRepository.findAllByDeviceId(dev).map { it.companyId }.toSet()
        } ?: emptySet()
        // 저장 공고로 학습한 취향(직군·회사)
        val savedIds = deviceId?.let { dev ->
            savedJobRepository.findAllByDeviceIdOrderByCreatedAtDesc(dev).map { it.jobId }
        } ?: emptyList()
        val savedJobs = if (savedIds.isEmpty()) emptyList() else jobRepository.findAllById(savedIds).toList()
        val savedCategories = savedJobs.flatMap { it.jobCategoryCodes ?: emptyList() }.toSet()
        val savedCompanies = savedJobs.map { it.companyId }.toSet()

        fun score(job: Job): Int {
            var s = 0
            if (job.companyId in myCompanies) s += 5
            if (myCategories.isNotEmpty() && job.jobCategoryCodes?.any { it in myCategories } == true) s += 3
            if (job.companyId in savedCompanies) s += 2
            if (savedCategories.isNotEmpty() && job.jobCategoryCodes?.any { it in savedCategories } == true) s += 2
            val age = java.time.temporal.ChronoUnit.DAYS.between(job.firstSeenAt, now)
            if (age <= 1) s += 2 else if (age <= 3) s += 1
            job.deadline?.let {
                if (java.time.temporal.ChronoUnit.DAYS.between(now, it) in 0..7) s += 1
            }
            return s
        }
        val scoreOf: Map<Job, Int> = pool.associateWith { score(it) }
        val rnd = java.util.Random()

        // 회사 다양성: 회사별 큐(점수 desc, 난수 tiebreak)를 한 건씩 라운드로빈 → 평탄화.
        fun companyDiverse(list: List<Job>): List<Job> {
            val queues = list.groupBy { it.companyId }.values
                .map { grp -> grp.sortedByDescending { scoreOf[it] ?: 0 } to rnd.nextInt() }
                .sortedWith(
                    compareByDescending<Pair<List<Job>, Int>> { it.first.maxOf { j -> scoreOf[j] ?: 0 } }
                        .thenBy { it.second },
                )
                .map { ArrayDeque(it.first) }
            val out = ArrayList<Job>(list.size)
            while (true) {
                var progressed = false
                for (q in queues) q.removeFirstOrNull()?.let { out.add(it); progressed = true }
                if (!progressed) break
            }
            return out
        }

        // 소스(greenhouse/pubinst/seoul…) 다양성: 소스별 '회사 다양성 리스트'를 다시 라운드로빈 → 소스도 섞임.
        fun roundRobin(jobs: List<Job>): List<Job> {
            if (jobs.isEmpty()) return emptyList()
            val sourceQueues = jobs.groupBy { it.id.substringBefore('-') }.values
                .map { ArrayDeque(companyDiverse(it)) to rnd.nextInt() }
                .sortedWith(
                    compareByDescending<Pair<ArrayDeque<Job>, Int>> { p -> p.first.maxOfOrNull { scoreOf[it] ?: 0 } ?: 0 }
                        .thenBy { it.second },
                )
                .map { it.first }
            val out = ArrayList<Job>(jobs.size)
            while (true) {
                var progressed = false
                for (q in sourceQueues) q.removeFirstOrNull()?.let { out.add(it); progressed = true }
                if (!progressed) break
            }
            return out
        }

        fun isInterest(job: Job) = job.companyId in myCompanies ||
            (myCategories.isNotEmpty() && job.jobCategoryCodes?.any { it in myCategories } == true)

        val (interest, discovery) = pool.partition { isInterest(it) }
        val interestRanked = roundRobin(interest)
        val discoveryRanked = roundRobin(discovery)

        // 관심:발견 3:1 (4칸 중 1칸은 발견). 한쪽이 비면 다른 쪽으로 채움.
        val merged = ArrayList<Job>(minOf(limit, pool.size))
        var i = 0
        var d = 0
        while (merged.size < limit && (i < interestRanked.size || d < discoveryRanked.size)) {
            val takeDiscovery = merged.size % 4 == 3
            when {
                takeDiscovery && d < discoveryRanked.size -> merged.add(discoveryRanked[d++])
                i < interestRanked.size -> merged.add(interestRanked[i++])
                d < discoveryRanked.size -> merged.add(discoveryRanked[d++])
                else -> break
            }
        }
        return JobListResponse(jobs = toDtos(merged))
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

    /** 주어진 ID들의 공고를 입력(최근 본) 순서대로 반환. '본 공고' 목록 등 클라가 가진 ID로 조회용. */
    fun byIds(ids: List<String>): JobListResponse {
        if (ids.isEmpty()) return JobListResponse(jobs = emptyList())
        val found = jobRepository.findAllById(ids).associateBy { it.id }
        val ordered = ids.mapNotNull { found[it] }   // 입력 순서(최근 본 순) 유지
        return JobListResponse(jobs = toDtos(ordered))
    }

    /**
     * 비슷한 공고. 직군(jobCategory) 겹침을 1순위, 같은 업종을 보조로 점수화해 추천.
     * 업종만 보던 기존 방식은 업종 없는 회사(서울·공공기관 다수)에서 빈 결과라 직군 기반으로 보강.
     */
    /**
     * 메인 표시용 kind. 오늘(KST) 처음 등장 = NEW(하루 유지). 마감 7일 이내 = CLOSING(앱 '마감임박').
     * 그 외 저장 kind가 UPDATE면 UPDATE, 나머지는 ACTIVE.
     */
    private fun displayKind(job: Job, todayKst: java.time.LocalDate): String {
        val firstSeen = job.firstSeenAt.atZoneSameInstant(kst).toLocalDate()
        if (firstSeen == todayKst) return "NEW"
        job.deadline?.atZoneSameInstant(kst)?.toLocalDate()?.let { dl ->
            val d = java.time.temporal.ChronoUnit.DAYS.between(todayKst, dl)
            if (d in 0..3) return "CLOSING"   // 마감임박 = D-3 이내
        }
        return if (job.kind == "UPDATE") "UPDATE" else "ACTIVE"
    }

    fun similar(id: String): JobListResponse {
        val base = jobRepository.findById(id).orElseThrow {
            NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        }
        val baseCats = base.jobCategoryCodes?.toSet().orEmpty()
        val baseIndustry = companyRepository.findById(base.companyId).orElse(null)?.industry

        // 후보 풀(최신순) — 자기 자신 제외. v0.1 규모라 메모리에서 점수화.
        val pool = jobRepository.findAllByIsActiveTrueOrderByFirstSeenAtDesc(PageRequest.of(0, 3000))
            .filter { it.id != id }
        val companies = loadCompanies(pool)

        // 점수 = 직군 겹침 수 ×100 + (같은 업종이면 10). 0점(무관)은 제외.
        // pool이 이미 최신순이고 sortedByDescending는 안정 정렬이라, 동점은 최신순 유지.
        val ranked = pool
            .map { job ->
                val cats = job.jobCategoryCodes?.toSet().orEmpty()
                val overlap = baseCats.count { it in cats }
                val sameIndustry = baseIndustry != null && companies[job.companyId]?.industry == baseIndustry
                job to (overlap * 100 + if (sameIndustry) 10 else 0)
            }
            .filter { it.second > 0 }
            .sortedByDescending { it.second }
            .map { it.first }
            .take(10)

        return JobListResponse(jobs = toDtos(ranked))
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
        val total = hits.size            // 실제 매칭 수(자르기 전) — 화면 "공고(N)"에 정확히 표시
        val shown = hits.take(limit)     // limit를 넉넉히 주면 사실상 전부 노출

        return JobSearchResponse(
            query = q,
            totalEstimate = total,
            jobs = toDtos(shown),
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
