package com.jobalert.backend.client.saramin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SaraminMapperTest {

    @Test
    fun `정상 raw 응답을 도메인 DTO로 매핑한다`() {
        val raw = SaraminJobRaw(
            id = "46123456",
            url = "https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=46123456",
            active = "1",
            company = SaraminCompanyWrapper(
                detail = SaraminCompanyDetail(name = "삼성전자", href = "https://www.samsung.com/sec/"),
            ),
            position = SaraminPosition(
                title = "2026 상반기 신입공채",
                industry = SaraminCodeName(code = "100", name = "전기·전자"),
                location = SaraminCodeName(code = "101000", name = "서울 강남구"),
                experienceLevel = SaraminExperienceLevel(code = "1", min = "0", max = "0", name = "신입"),
                requiredEducationLevel = SaraminCodeName(code = "8", name = "대학교(4년)"),
            ),
            keyword = "Java, Kotlin , Spring",
            salary = SaraminCodeName(code = "0", name = "회사내규에 따름"),
            postingTimestamp = "1716595200",
            expirationTimestamp = "1718064000",
        )

        val domain = SaraminMapper.toDomain(raw)

        requireNotNull(domain)
        assertEquals("saramin-46123456", domain.externalId)
        assertEquals("2026 상반기 신입공채", domain.title)
        assertEquals("삼성전자", domain.companyName)
        assertEquals("https://www.samsung.com/sec/", domain.companyHomepage)
        assertEquals("전기·전자", domain.industry)
        assertEquals("신입", domain.experience)
        assertEquals("대학교(4년)", domain.education)
        assertEquals(1716595200L, domain.postingDateEpoch)
        assertEquals(1718064000L, domain.deadlineEpoch)
        assertEquals(listOf("Java", "Kotlin", "Spring"), domain.keywords)
    }

    @Test
    fun `id가 비어있으면 null을 반환한다`() {
        val raw = SaraminJobRaw(
            id = null,
            company = SaraminCompanyWrapper(detail = SaraminCompanyDetail(name = "X")),
            position = SaraminPosition(title = "T"),
        )
        assertNull(SaraminMapper.toDomain(raw))
    }

    @Test
    fun `회사명이 비어있으면 null을 반환한다`() {
        val raw = SaraminJobRaw(
            id = "1",
            company = SaraminCompanyWrapper(detail = SaraminCompanyDetail(name = " ")),
            position = SaraminPosition(title = "T"),
        )
        assertNull(SaraminMapper.toDomain(raw))
    }

    @Test
    fun `타이틀이 비어있으면 null을 반환한다`() {
        val raw = SaraminJobRaw(
            id = "1",
            company = SaraminCompanyWrapper(detail = SaraminCompanyDetail(name = "X")),
            position = SaraminPosition(title = ""),
        )
        assertNull(SaraminMapper.toDomain(raw))
    }

    @Test
    fun `keyword가 null이면 빈 리스트를 반환한다`() {
        val raw = SaraminJobRaw(
            id = "1",
            company = SaraminCompanyWrapper(detail = SaraminCompanyDetail(name = "X")),
            position = SaraminPosition(title = "T"),
            keyword = null,
        )
        val domain = SaraminMapper.toDomain(raw)
        requireNotNull(domain)
        assertTrue(domain.keywords.isEmpty())
    }
}
