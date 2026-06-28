package com.jobalert.backend.client.source.greeting

import com.jobalert.backend.client.source.GreetingWorkspace
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 그리팅 openings 실 응답(2026-06-28 라이브 발췌)을 매퍼가 제대로 변환하는지.
 */
class GreetingMapperTest {

    private val oliveyoung = GreetingWorkspace("oliveyoung", 10501, "CJ올리브영", homepage = "oliveyoung.co.kr")

    @Test
    fun `경력 공고 - 회사명은 displayName, 원문은 서브도메인+openingId`() {
        val o = GreetingOpening(
            openingId = 224469,
            title = "Windows Application Engineer (POS 시스템)",
            openDate = "2026-06-25T09:27:42Z",
            dueDate = null,
            group = GreetingGroup("CJ올리브영"),
            openingJobPosition = GreetingJobPositionWrap(
                listOf(
                    GreetingJobPosition(
                        workspaceOccupation = GreetingOccupation("IT"),
                        workspaceJob = GreetingJob("Back-end Engineering"),
                        workspacePlace = GreetingPlace("CJ올리브영", "대한민국 서울특별시 용산구 한강대로 372"),
                        jobPositionCareer = GreetingCareer("EXPERIENCED"),
                        jobPositionEmployment = GreetingEmployment("FULL_TIME_WORKER"),
                    ),
                ),
            ),
        )
        val raw = GreetingMapper.toRawJob(oliveyoung, o)
        assertNotNull(raw)
        assertEquals("greeting", raw.source)
        assertEquals("greeting-10501-224469", raw.externalId)
        assertEquals("CJ올리브영", raw.companyName)
        assertEquals("경력", raw.experience)
        assertEquals("IT", raw.department)
        assertEquals("대한민국 서울특별시 용산구 한강대로 372", raw.location)
        assertEquals("https://oliveyoung.career.greetinghr.com/o/224469", raw.originalUrl)
        assertNotNull(raw.postingDateEpoch)
        assertNull(raw.deadlineEpoch) // dueDate null = 상시
        assertTrue(raw.keywords.contains("Back-end Engineering"))
    }

    @Test
    fun `마감일 있는 공고 - dueDate 파싱, division은 keywords로`() {
        val o = GreetingOpening(
            openingId = 100,
            title = "신입 MD 채용",
            openDate = "2026-06-01T00:00:00Z",
            dueDate = "2026-06-30T14:59:59Z",
            workspaceDivision = GreetingDivision("무신사"),
            openingJobPosition = GreetingJobPositionWrap(
                listOf(
                    GreetingJobPosition(
                        workspaceOccupation = GreetingOccupation("MD"),
                        jobPositionCareer = GreetingCareer("NEW_COMER"),
                    ),
                ),
            ),
        )
        val raw = GreetingMapper.toRawJob(GreetingWorkspace("musinsa", 1455, "무신사"), o)
        assertNotNull(raw)
        assertEquals("무신사", raw.companyName)
        assertEquals("신입", raw.experience)
        assertNotNull(raw.deadlineEpoch)
        assertTrue(raw.deadlineEpoch!! > raw.postingDateEpoch!!)
        assertTrue(raw.keywords.contains("무신사"))
    }

    @Test
    fun `careerType 없고 인턴 고용형태면 인턴으로 표기`() {
        val o = GreetingOpening(
            openingId = 7,
            title = "여름 인턴",
            openingJobPosition = GreetingJobPositionWrap(
                listOf(GreetingJobPosition(jobPositionEmployment = GreetingEmployment("INTERN_WORKER"))),
            ),
        )
        val raw = GreetingMapper.toRawJob(oliveyoung, o)
        assertNotNull(raw)
        assertEquals("인턴", raw.experience)
        assertEquals("한국", raw.location) // place 없으면 폴백
    }

    @Test
    fun `originHost 지정 시 원문 링크는 커스텀 도메인`() {
        val jyp = GreetingWorkspace("jype", 12286, "JYP엔터테인먼트", originHost = "recruit.jype.com")
        val raw = GreetingMapper.toRawJob(jyp, GreetingOpening(openingId = 555, title = "댄서 채용"))
        assertNotNull(raw)
        assertEquals("https://recruit.jype.com/o/555", raw.originalUrl)
    }

    @Test
    fun `openingId나 title 없으면 null`() {
        assertNull(GreetingMapper.toRawJob(oliveyoung, GreetingOpening(openingId = null, title = "x")))
        assertNull(GreetingMapper.toRawJob(oliveyoung, GreetingOpening(openingId = 1, title = "  ")))
    }
}
