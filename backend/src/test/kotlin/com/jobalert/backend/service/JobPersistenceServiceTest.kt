package com.jobalert.backend.service

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.Job
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.JobRepository
import io.mockk.every
import io.mockk.mockk
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [JobPersistenceService] diff·적재 단위 테스트. DB·Spring 없이 MockK로 리포지토리를 모킹한다.
 * 시각은 2026-06-05 00:00 UTC 로 고정(Clock.fixed) — CLOSING 판정을 결정적으로 재현.
 */
class JobPersistenceServiceTest {

    private val jobRepo = mockk<JobRepository>()
    private val companyRepo = mockk<CompanyRepository>()
    private val matcher = mockk<CompanyMatcher>()
    private val logoResolver = mockk<CompanyLogoResolver>(relaxed = true)
    private val clock = Clock.fixed(Instant.parse("2026-06-05T00:00:00Z"), ZoneOffset.UTC)
    private val now = OffsetDateTime.now(clock)

    private val service = JobPersistenceService(jobRepo, companyRepo, matcher, logoResolver, JobCategoryClassifier(), clock)

    // save 로 넘어온 엔티티를 들여다보기 위한 캡처 버퍼.
    private val savedJobs = mutableListOf<Job>()
    private val savedCompanies = mutableListOf<Company>()

    @BeforeTest
    fun setup() {
        savedJobs.clear()
        savedCompanies.clear()
        every { jobRepo.save(capture(savedJobs)) } answers { firstArg() }
        every { companyRepo.save(capture(savedCompanies)) } answers {
            firstArg<Company>().also { if (it.id == null) it.id = 99L }
        }
        // 기본값: 만료 스윕 시 활성 공고 없음. 개별 테스트가 필요하면 덮어쓴다.
        every { jobRepo.findAllBySourceAndIsActiveTrue(any()) } returns emptyList()
    }

    private fun raw(
        source: String = "greenhouse",
        externalId: String = "greenhouse-coupang-1",
        title: String = "백엔드 엔지니어",
        company: String = "쿠팡",
        deadlineEpoch: Long? = null,
    ) = RawJobPosting(
        source = source,
        externalId = externalId,
        title = title,
        companyName = company,
        deadlineEpoch = deadlineEpoch,
    )

    @Test
    fun `신규 공고는 INSERT 되고 kind=NEW`() {
        every { matcher.matchOrNull("쿠팡") } returns 10L
        every { jobRepo.findBySourceAndSourceExternalId("greenhouse", "greenhouse-coupang-1") } returns null

        val r = service.persist(listOf(raw()))

        assertEquals(1, r.inserted)
        val job = savedJobs.single()
        assertEquals("NEW", job.kind)
        assertEquals(10L, job.companyId)
        assertTrue(job.isActive)
    }

    @Test
    fun `회사 매칭 미스면 자동 생성하고 isApproved=false`() {
        every { matcher.matchOrNull("처음보는회사") } returns null
        every { jobRepo.findBySourceAndSourceExternalId(any(), any()) } returns null

        val r = service.persist(listOf(raw(company = "처음보는회사", externalId = "greenhouse-x-1")))

        assertEquals(1, r.companiesCreated)
        val company = savedCompanies.single()
        assertFalse(company.isApproved)
        assertEquals("처음보는회사", company.name)
    }

    @Test
    fun `같은 배치에 같은 회사 두 건이면 회사 1번만 생성`() {
        every { matcher.matchOrNull("처음보는회사") } returns null
        every { jobRepo.findBySourceAndSourceExternalId(any(), any()) } returns null

        val r = service.persist(
            listOf(
                raw(company = "처음보는회사", externalId = "greenhouse-x-1"),
                raw(company = "처음보는회사", externalId = "greenhouse-x-2"),
            ),
        )

        assertEquals(1, r.companiesCreated)
        assertEquals(2, r.inserted)
        assertEquals(1, savedCompanies.size)
    }

    @Test
    fun `제목 바뀌면 UPDATE`() {
        every { matcher.matchOrNull("쿠팡") } returns 10L
        val existing = Job(
            id = "greenhouse-coupang-1", companyId = 10L, source = "greenhouse",
            sourceExternalId = "greenhouse-coupang-1", title = "옛 제목", kind = "NEW",
        )
        every { jobRepo.findBySourceAndSourceExternalId("greenhouse", "greenhouse-coupang-1") } returns existing
        every { jobRepo.findAllBySourceAndIsActiveTrue("greenhouse") } returns listOf(existing)

        val r = service.persist(listOf(raw(title = "새 제목")))

        assertEquals(1, r.updated)
        assertEquals("UPDATE", existing.kind)
        assertEquals("새 제목", existing.title)
    }

    @Test
    fun `마감 2일 전이면 CLOSING (UPDATE보다 우선)`() {
        every { matcher.matchOrNull("한국전력공사") } returns 20L
        val deadline2DaysLater = now.plusDays(2).toEpochSecond()
        val existing = Job(
            id = "pubinst-1", companyId = 20L, source = "pubinst",
            sourceExternalId = "pubinst-1", title = "신입 채용", kind = "NEW",
        )
        every { jobRepo.findBySourceAndSourceExternalId("pubinst", "pubinst-1") } returns existing
        every { jobRepo.findAllBySourceAndIsActiveTrue("pubinst") } returns listOf(existing)

        val r = service.persist(
            listOf(raw(source = "pubinst", externalId = "pubinst-1", title = "신입 채용 변경", company = "한국전력공사", deadlineEpoch = deadline2DaysLater)),
        )

        assertEquals(1, r.closing)
        assertEquals(0, r.updated)
        assertEquals("CLOSING", existing.kind)
    }

    @Test
    fun `변화 없으면 UNCHANGED 이고 kind 유지`() {
        every { matcher.matchOrNull("쿠팡") } returns 10L
        val existing = Job(
            id = "greenhouse-coupang-1", companyId = 10L, source = "greenhouse",
            sourceExternalId = "greenhouse-coupang-1", title = "백엔드 엔지니어", kind = "UPDATE",
        )
        every { jobRepo.findBySourceAndSourceExternalId("greenhouse", "greenhouse-coupang-1") } returns existing
        every { jobRepo.findAllBySourceAndIsActiveTrue("greenhouse") } returns listOf(existing)

        val r = service.persist(listOf(raw()))

        assertEquals(1, r.unchanged)
        assertEquals("UPDATE", existing.kind) // 기존 kind 유지
    }

    @Test
    fun `이번 수집에서 사라진 active 공고는 만료된다`() {
        every { matcher.matchOrNull("쿠팡") } returns 10L
        val stillThere = Job(
            id = "greenhouse-coupang-1", companyId = 10L, source = "greenhouse",
            sourceExternalId = "greenhouse-coupang-1", title = "백엔드 엔지니어", kind = "NEW",
        )
        val gone = Job(
            id = "greenhouse-coupang-2", companyId = 10L, source = "greenhouse",
            sourceExternalId = "greenhouse-coupang-2", title = "사라진 공고", kind = "NEW",
        )
        every { jobRepo.findBySourceAndSourceExternalId("greenhouse", "greenhouse-coupang-1") } returns stillThere
        every { jobRepo.findAllBySourceAndIsActiveTrue("greenhouse") } returns listOf(stillThere, gone)

        // 이번 수집엔 coupang-1 만 들어옴
        val r = service.persist(listOf(raw()))

        assertEquals(1, r.expired)
        assertFalse(gone.isActive)
        assertNotNull(gone.closedAt)
        assertTrue(stillThere.isActive)
    }

    @Test
    fun `데이터를 한 건도 안 준 소스는 만료 스윕하지 않는다 (장애 안전장치)`() {
        val r = service.persist(emptyList())

        assertEquals(0, r.expired)
        assertEquals(0, r.inserted)
    }

    @Test
    fun `회사명이 비면 공고를 스킵한다`() {
        val r = service.persist(listOf(raw(company = "   ")))

        assertEquals(1, r.skippedNoCompany)
        assertEquals(0, r.inserted)
    }
}
