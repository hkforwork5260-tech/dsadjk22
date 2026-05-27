package com.jobalert.backend.service

import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.CompanyAlias
import com.jobalert.backend.repository.CompanyAliasRepository
import com.jobalert.backend.repository.CompanyRepository
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompanyMatcherTest {

    private val companyRepo = mockk<CompanyRepository>()
    private val aliasRepo = mockk<CompanyAliasRepository>()
    private val matcher = CompanyMatcher(companyRepo, aliasRepo)

    @Test
    fun `정규화 후 exact match 성공`() {
        every { companyRepo.findByNameNormalized("카카오") } returns Company(id = 42L, name = "카카오", nameNormalized = "카카오")

        val id = matcher.matchOrNull("(주) 카카오")

        assertEquals(42L, id)
    }

    @Test
    fun `name 미스 시 alias로 fallback`() {
        // "Kakao Corp." → Corp 접미 제거 후 normalize="kakao"
        every { companyRepo.findByNameNormalized("kakao") } returns null
        every { aliasRepo.findByAliasNormalized("kakao") } returns
            CompanyAlias(id = 1L, companyId = 42L, alias = "Kakao", aliasNormalized = "kakao")

        val id = matcher.matchOrNull("Kakao Corp.")

        assertEquals(42L, id)
    }

    @Test
    fun `name·alias 둘 다 미스면 null`() {
        every { companyRepo.findByNameNormalized(any()) } returns null
        every { aliasRepo.findByAliasNormalized(any()) } returns null

        assertNull(matcher.matchOrNull("처음보는회사"))
    }

    @Test
    fun `null·빈 입력은 null 반환 (repo 호출 없음)`() {
        assertNull(matcher.matchOrNull(null))
        assertNull(matcher.matchOrNull(""))
        assertNull(matcher.matchOrNull("   "))
        // 모든 마커 제거 후 빈 문자열인 경우도 null
        assertNull(matcher.matchOrNull("(주)"))
    }

    @Test
    fun `matchBatch — matched·unmatched 정확히 분리`() {
        every { companyRepo.findByNameNormalized("카카오") } returns Company(id = 1L, name = "카카오", nameNormalized = "카카오")
        every { companyRepo.findByNameNormalized("네이버") } returns Company(id = 2L, name = "네이버", nameNormalized = "네이버")
        every { companyRepo.findByNameNormalized("듣보잡회사") } returns null
        every { aliasRepo.findByAliasNormalized("듣보잡회사") } returns null

        val summary = matcher.matchBatch(listOf("(주)카카오", "네이버", "듣보잡회사", "(주)카카오"))

        assertEquals(2, summary.matched.size)
        assertEquals(1L, summary.matched["(주)카카오"])
        assertEquals(2L, summary.matched["네이버"])
        assertEquals(listOf("듣보잡회사"), summary.unmatched)
    }
}
