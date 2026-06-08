package com.jobalert.backend.service

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.Job
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * 수집기가 모아 온 [RawJobPosting] 묶음을 `jobs` 테이블에 적재하고, 어제 대비 diff 라벨을 붙인다.
 *
 * 앱의 핵심 가치("어제 대비 NEW/UPDATE/CLOSING 자동 라벨링")의 빠진 절반.
 * 수집은 [HybridCollectorService]가 메모리까지 가져오고, 그걸 영속화 + 라벨링하는 게 여기 책임.
 *
 * diff 규칙 (공고 식별 키 = `source` + `sourceExternalId`):
 *  - 이번 수집에 있고 DB에 없음           → INSERT, kind = NEW
 *  - 양쪽에 있고 제목/마감일 바뀜          → UPDATE (단 CLOSING 조건이면 CLOSING 우선)
 *  - 양쪽에 있고 마감 [CLOSING_WINDOW_DAYS]일 이내 → CLOSING (마감일 있는 소스만 — 기재부 등)
 *  - 양쪽에 있고 변화 없음                 → kind 유지 (lastSeenAt만 갱신)
 *  - DB엔 active인데 이번 수집에서 사라짐    → isActive=false, closedAt=now (만료)
 *
 * 회사 매칭: [CompanyMatcher]로 회사 풀에 붙이고, **미스면 자동 생성**(isApproved=false).
 * v0.1 하이브리드는 수집 회사 대부분이 풀에 없으므로 자동 생성이 기본. admin 검수는 isApproved 플래그로 후속.
 *
 * 만료 스윕 안전장치: 이번 수집에서 **공고를 1건 이상 준 소스**만 스윕 대상. 0건 소스(진짜 비었든
 * API 장애로 빈 응답이든)는 건드리지 않는다 → 일시적 장애가 전체 공고를 닫아버리는 사고 방지.
 */
@Service
class JobPersistenceService(
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val companyMatcher: CompanyMatcher,
    private val logoResolver: CompanyLogoResolver,
    private val classifier: JobCategoryClassifier,
    private val experienceClassifier: ExperienceClassifier,
    private val clock: Clock,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun persist(postings: List<RawJobPosting>): PersistResult {
        val now = OffsetDateTime.now(clock)

        // 같은 배치 안에서 동일 회사를 두 번 INSERT 하지 않도록 정규화명 → companyId 캐시.
        val companyCache = HashMap<String, Long>()
        val createdCompanyIds = HashSet<Long>()
        // 소스별로 이번 수집에서 본 externalId 집합 — 만료 스윕에 사용.
        val seenBySource = HashMap<String, MutableSet<String>>()

        var inserted = 0
        var updated = 0
        var unchanged = 0
        var closing = 0
        var skippedNoCompany = 0

        for (raw in postings) {
            val companyId = resolveCompanyId(raw, companyCache, createdCompanyIds, now)
            if (companyId == null) {
                skippedNoCompany++
                continue
            }
            seenBySource.getOrPut(raw.source) { mutableSetOf() }.add(raw.externalId)

            val existing = jobRepository.findBySourceAndSourceExternalId(raw.source, raw.externalId)
            if (existing == null) {
                jobRepository.save(newJob(raw, companyId, now))
                inserted++
            } else {
                when (applyDiff(existing, raw, now)) {
                    DiffOutcome.CLOSING -> closing++
                    DiffOutcome.UPDATED -> updated++
                    DiffOutcome.UNCHANGED -> unchanged++
                }
                jobRepository.save(existing)
            }
        }

        val expired = sweepExpired(seenBySource, now)

        val result = PersistResult(
            inserted = inserted,
            updated = updated,
            closing = closing,
            unchanged = unchanged,
            expired = expired,
            companiesCreated = createdCompanyIds.size,
            skippedNoCompany = skippedNoCompany,
        )
        log.info("job persist 완료: {}", result)
        return result
    }

    /** 회사 풀 매칭 → 미스면 자동 생성(isApproved=false). 회사명이 비면 null(공고 스킵). */
    private fun resolveCompanyId(
        raw: RawJobPosting,
        cache: MutableMap<String, Long>,
        createdIds: MutableSet<Long>,
        now: OffsetDateTime,
    ): Long? {
        val normalized = CompanyNameNormalizer.normalize(raw.companyName)
        if (normalized.isBlank()) return null

        cache[normalized]?.let { return it }
        // matchOrNull 은 승인 여부와 무관하게 name_normalized 로 찾으므로,
        // 지난 수집에서 자동 생성한(미승인) 회사도 여기서 재매칭된다 → 중복 생성 없음.
        companyMatcher.matchOrNull(raw.companyName)?.let { id ->
            cache[normalized] = id
            // 기존 회사 규모가 비어 있으면 소스 기반으로 보정(서울 441건 등 size=null → small).
            inferSize(raw.source)?.let { inferred ->
                val co = companyRepository.findById(id).orElse(null)
                if (co != null && co.size.isNullOrBlank()) {
                    co.size = inferred
                    co.updatedAt = now
                    companyRepository.save(co)
                }
            }
            return id
        }

        val company = Company(
            name = raw.companyName.trim(),
            nameNormalized = normalized,
            homepageUrl = raw.companyHomepage,
            domain = logoResolver.resolveDomain(raw.companyName, raw.companyHomepage),
            logoUrl = logoResolver.resolveLogoUrl(raw.companyName, raw.companyHomepage),
            // 규모는 출처로 추론(소스에 회사별 규모 데이터가 없어 근사): 공공기관=공기업,
            // 서울 일자리포털=중소(압도적 다수), Greenhouse/Lever=대기업(빅테크). 그 외 null.
            size = inferSize(raw.source),
            isApproved = false,
            createdAt = now,
            updatedAt = now,
        )
        val id = companyRepository.save(company).id!!
        cache[normalized] = id
        createdIds += id
        return id
    }

    /** 소스 → 회사 규모 코드 추론(근사). 회사별 정밀 규모 데이터가 없어 출처로 판단. */
    private fun inferSize(source: String): String? = when (source) {
        "public-institution" -> "public"
        "seoul" -> "small"
        "greenhouse", "lever" -> "large_corp"
        else -> null
    }

    private fun newJob(raw: RawJobPosting, companyId: Long, now: OffsetDateTime) = Job(
        id = stableId(raw),
        companyId = companyId,
        source = raw.source,
        sourceExternalId = raw.externalId,
        title = raw.title,
        kind = "NEW",
        location = raw.location,
        experience = experienceClassifier.classify(raw.experience, raw.title),
        education = raw.education,
        salary = raw.salary,
        tags = raw.tags.takeIf { it.isNotEmpty() },
        jobCategoryCodes = classifier.classify(raw.title, raw.department, raw.keywords, raw.description),
        description = raw.description,
        postingDate = raw.postingDateEpoch?.toUtcOdt(),
        deadline = raw.deadlineEpoch?.toUtcOdt(),
        originalUrl = raw.originalUrl,
        isActive = true,
        firstSeenAt = now,
        lastSeenAt = now,
        createdAt = now,
        updatedAt = now,
    )

    /**
     * DB의 모든 공고를 분류기로 재분류(소스 재수집 없이). 분류 규칙 개선 후 즉시 반영용 — OOM 안전.
     * department는 저장 안 돼 있어 title + tags + description(본문)으로 분류. 미매칭은 "etc"(기타).
     */
    @Transactional
    fun reclassifyAll(): ReclassifyResult {
        var updated = 0
        var etc = 0
        val all = jobRepository.findAll()
        for (job in all) {
            val newCats = classifier.classify(job.title, null, job.tags ?: emptyList(), job.description)
            if (newCats == listOf("etc")) etc++
            if (job.jobCategoryCodes != newCats) {
                job.jobCategoryCodes = newCats
                jobRepository.save(job)
                updated++
            }
        }
        val result = ReclassifyResult(total = all.size, updated = updated, etc = etc)
        log.info("reclassify 완료: {}", result)
        return result
    }

    data class ReclassifyResult(val total: Int, val updated: Int, val etc: Int)

    /** 기존 공고에 변경을 반영하고 새 kind 를 정한다. 호출자가 save 한다. */
    private fun applyDiff(job: Job, raw: RawJobPosting, now: OffsetDateTime): DiffOutcome {
        val newDeadline = raw.deadlineEpoch?.toUtcOdt()
        val titleChanged = job.title != raw.title
        val deadlineChanged = !sameInstant(job.deadline, newDeadline)
        val changed = titleChanged || deadlineChanged

        // 필드 갱신 (소스가 최신이라고 보고 덮어쓴다).
        job.title = raw.title
        job.deadline = newDeadline
        job.location = raw.location
        job.originalUrl = raw.originalUrl
        // 본문·학력·급여는 소스가 주면 갱신, 안 주면(null) 기존 유지 — 다른 소스가 같은 공고를 비우지 않게.
        raw.description?.let { job.description = it }
        raw.education?.let { job.education = it }
        raw.salary?.let { job.salary = it }
        raw.tags.takeIf { it.isNotEmpty() }?.let { job.tags = it }
        // 만료(isActive=false)됐던 공고가 다시 수집되면 재활성화 — 일시적 미노출/장애 후 복귀 대응.
        if (!job.isActive) {
            job.isActive = true
            job.closedAt = null
        }
        job.experience = experienceClassifier.classify(raw.experience, raw.title)
        // 기존 공고도 재분류 — 분류 규칙이 개선되면 다음 수집에서 반영됨.
        job.jobCategoryCodes = classifier.classify(raw.title, raw.department, raw.keywords, raw.description)
        raw.postingDateEpoch?.toUtcOdt()?.let { job.postingDate = it }
        job.lastSeenAt = now
        job.updatedAt = now

        val outcome = when {
            isClosing(newDeadline, now) -> DiffOutcome.CLOSING
            changed -> DiffOutcome.UPDATED
            else -> DiffOutcome.UNCHANGED
        }
        job.kind = when (outcome) {
            DiffOutcome.CLOSING -> "CLOSING"
            DiffOutcome.UPDATED -> "UPDATE"
            // 변화 없음 → 일반(ACTIVE)으로. 어제 NEW였던 공고가 오늘도 NEW로 남는 누적 방지.
            // "오늘 새 공고(NEW)"는 이번 수집에서 처음 들어온(INSERT) 것만 유지된다.
            DiffOutcome.UNCHANGED -> "ACTIVE"
        }
        return outcome
    }

    /** 이번 수집에서 데이터를 준 소스에 한해, 더 이상 안 보이는 active 공고를 닫는다. */
    private fun sweepExpired(seenBySource: Map<String, Set<String>>, now: OffsetDateTime): Int {
        var expired = 0
        for ((source, seenIds) in seenBySource) {
            for (job in jobRepository.findAllBySourceAndIsActiveTrue(source)) {
                if (job.sourceExternalId !in seenIds) {
                    job.isActive = false
                    job.closedAt = now
                    job.updatedAt = now
                    jobRepository.save(job)
                    expired++
                }
            }
        }
        return expired
    }

    /** 마감일이 [now, now+window) 구간이면 마감 임박. 마감일 없으면(Greenhouse/Lever) false. */
    private fun isClosing(deadline: OffsetDateTime?, now: OffsetDateTime): Boolean {
        if (deadline == null) return false
        return !deadline.isBefore(now) && deadline.isBefore(now.plusDays(CLOSING_WINDOW_DAYS))
    }

    private fun sameInstant(a: OffsetDateTime?, b: OffsetDateTime?): Boolean =
        a?.toInstant() == b?.toInstant()

    private fun Long.toUtcOdt(): OffsetDateTime =
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneOffset.UTC)

    /** PK(≤64자). externalId 가 64자 이내면 그대로(가독성), 넘으면 SHA-256 hex. */
    private fun stableId(raw: RawJobPosting): String {
        if (raw.externalId.length <= 64) return raw.externalId
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.externalId.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private enum class DiffOutcome { CLOSING, UPDATED, UNCHANGED }

    data class PersistResult(
        val inserted: Int,
        val updated: Int,
        val closing: Int,
        val unchanged: Int,
        val expired: Int,
        val companiesCreated: Int,
        val skippedNoCompany: Int,
    )

    companion object {
        /** 마감 임박(CLOSING) 판정 창. 마감 N일 이내면 CLOSING. */
        const val CLOSING_WINDOW_DAYS = 3L
    }
}
