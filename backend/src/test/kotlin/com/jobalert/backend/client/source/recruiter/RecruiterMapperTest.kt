package com.jobalert.backend.client.source.recruiter

import com.jobalert.backend.client.source.RecruiterTenant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * recruiter jobflex 실 응답(2026-06-27 라이브 발췌)을 매퍼가 제대로 변환하는지.
 * 단일사(KT)와 그룹허브(HL그룹: classificationCode=계열사명) 두 케이스.
 */
class RecruiterMapperTest {

    @Test
    fun `단일사 - 회사명은 displayName, 경력구분은 태그`() {
        val kt = RecruiterTenant("kt", "KT")
        val p = RecruiterPosition(
            positionSn = 120012,
            title = "[kt telecop] 2026년 산업안전 분야 채용",
            startDateTime = "2026-06-26T00:00:00",
            endDateTime = "2026-07-05T23:59:59",
            careerType = "CAREER",
            classificationCode = "경력",
            dday = 7,
            tagList = listOf(RecruiterTag(14177, "경력")),
        )
        val raw = RecruiterMapper.toRawJob(kt, p)
        assertNotNull(raw)
        assertEquals("recruiter", raw.source)
        assertEquals("recruiter-kt-120012", raw.externalId)
        assertEquals("KT", raw.companyName)
        assertEquals("경력", raw.experience)
        assertEquals("https://kt.recruiter.co.kr/career/home?positionSn=120012", raw.originalUrl)
        assertEquals("한국", raw.location)
        assertNotNull(raw.postingDateEpoch)
        assertNotNull(raw.deadlineEpoch)
        assertTrue(raw.deadlineEpoch!! > raw.postingDateEpoch!!)
    }

    @Test
    fun `그룹허브 - classificationCode가 계열사명이 된다`() {
        val hl = RecruiterTenant("hlcompany", "HL그룹", groupHub = true)
        val p = RecruiterPosition(
            positionSn = 119799,
            title = "[만도브로제] R&D(모터설계) 경력사원 채용",
            startDateTime = "2026-06-20T00:00:00",
            endDateTime = null,
            careerType = "CAREER",
            classificationCode = "만도브로제",
            dday = 10,
            tagList = emptyList(),
        )
        val raw = RecruiterMapper.toRawJob(hl, p)
        assertNotNull(raw)
        assertEquals("만도브로제", raw.companyName, "그룹허브는 계열사명을 회사로")
        assertEquals("경력", raw.experience, "태그 없으면 careerType=CAREER→경력")
        assertEquals("recruiter-hlcompany-119799", raw.externalId)
        assertEquals(null, raw.deadlineEpoch, "마감 없으면 null(상시)")
    }

    @Test
    fun `그룹허브 - classificationCode가 경력라벨이면 제목 접두를 회사로`() {
        // careerhyundai(현대카드)처럼 허브인데 classificationCode가 "경력"인 경우.
        val ch = RecruiterTenant("careerhyundai", "현대카드", groupHub = true)
        val p = RecruiterPosition(
            positionSn = 5001,
            title = "[현대커머셜] 리스크관리 경력 채용",
            classificationCode = "경력",
            careerType = "CAREER",
        )
        val raw = RecruiterMapper.toRawJob(ch, p)
        assertNotNull(raw)
        assertEquals("현대커머셜", raw.companyName, "경력라벨 대신 제목 [계열사] 접두 사용")
        assertEquals("경력", raw.experience)
    }

    @Test
    fun `positionSn이나 title 없으면 스킵`() {
        val kt = RecruiterTenant("kt", "KT")
        assertEquals(null, RecruiterMapper.toRawJob(kt, RecruiterPosition(positionSn = null, title = "x")))
        assertEquals(null, RecruiterMapper.toRawJob(kt, RecruiterPosition(positionSn = 1, title = "  ")))
    }
}
