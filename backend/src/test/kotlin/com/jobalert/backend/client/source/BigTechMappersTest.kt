package com.jobalert.backend.client.source

import com.jobalert.backend.client.source.cj.CjJob
import com.jobalert.backend.client.source.cj.CjMapper
import com.jobalert.backend.client.source.lg.LgJobNotice
import com.jobalert.backend.client.source.lg.LgMapper
import com.jobalert.backend.client.source.naver.NaverJob
import com.jobalert.backend.client.source.naver.NaverMapper
import com.jobalert.backend.client.source.toss.TossJobGroup
import com.jobalert.backend.client.source.toss.TossMapper
import com.jobalert.backend.client.source.toss.TossLocation
import com.jobalert.backend.client.source.toss.TossMetadata
import com.jobalert.backend.client.source.toss.TossPrimaryJob
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * 토스·네이버·LG·CJ 매퍼 — 2026-06-28 라이브 응답 발췌 기준.
 */
class BigTechMappersTest {

    @Test
    fun `토스 - 자회사 metadata가 회사명, absolute_url이 원문`() {
        val g = TossJobGroup(
            id = 6008890003,
            title = "Account Manager",
            primaryJob = TossPrimaryJob(
                title = "Account Manager [FacePay Business]",
                absoluteUrl = "https://toss.im/career/job-detail?gh_jid=6008890003",
                location = TossLocation("Seoul"),
                firstPublished = "2024-06-03T08:40:26-04:00",
                applicationDeadline = null,
                metadata = listOf(
                    TossMetadata("Employment_Type", "정규직"),
                    TossMetadata("포지션의 소속 자회사를 선택해 주세요.", "토스뱅크"),
                    TossMetadata("커리어 페이지 노출 Job Category 값을 선택해주세요", "Sales"),
                ),
            ),
        )
        val raw = TossMapper.toRawJob(g)
        assertNotNull(raw)
        assertEquals("toss", raw.source)
        assertEquals("toss-6008890003", raw.externalId)
        assertEquals("토스뱅크", raw.companyName)
        assertEquals("Seoul", raw.location)
        assertEquals("Sales", raw.department)
        assertNotNull(raw.postingDateEpoch)
        assertNull(raw.deadlineEpoch)
        assertEquals("https://toss.im/career/job-detail?gh_jid=6008890003", raw.originalUrl)
    }

    @Test
    fun `토스 - 자회사 없으면 토스 폴백, 인턴 고용형태 반영`() {
        val g = TossJobGroup(
            id = 1, title = "x",
            primaryJob = TossPrimaryJob(
                title = "여름 인턴",
                metadata = listOf(TossMetadata("Employment_Type", "체험형 인턴")),
            ),
        )
        val raw = TossMapper.toRawJob(g)
        assertNotNull(raw)
        assertEquals("토스", raw.companyName)
        assertEquals("인턴", raw.experience)
        assertEquals("한국", raw.location)
    }

    @Test
    fun `네이버 - NAVER는 네이버로, jobDetailLink가 원문`() {
        val j = NaverJob(
            annoId = 30005031,
            sysCompanyCdNm = "NAVER",
            annoSubject = "[NAVER] 플레이스 프로모션·캠페인 운영 (계약)",
            entTypeCdNm = "경력",
            staYmdTime = "2026.06.15 14:00:00",
            endYmdTime = "2026.06.29 10:00:00",
            classCdNm = "Service & Business",
            subJobCdNm = "Content Development",
            jobDetailLink = "https://recruit.navercorp.com/rcrt/view.do?annoId=30005031",
        )
        val raw = NaverMapper.toRawJob(j)
        assertNotNull(raw)
        assertEquals("naver-30005031", raw.externalId)
        assertEquals("네이버", raw.companyName)
        assertEquals("경력", raw.experience)
        assertEquals("Service & Business", raw.department)
        assertNotNull(raw.postingDateEpoch)
        assertNotNull(raw.deadlineEpoch)
        assertTrue(raw.deadlineEpoch!! > raw.postingDateEpoch!!)
        assertEquals("https://recruit.navercorp.com/rcrt/view.do?annoId=30005031", raw.originalUrl)
        assertTrue(raw.keywords.contains("Content Development"))
    }

    @Test
    fun `네이버 - 계열사명은 그대로 유지`() {
        val raw = NaverMapper.toRawJob(NaverJob(annoId = 2, sysCompanyCdNm = "NAVER WEBTOON", annoSubject = "웹툰 PD"))
        assertNotNull(raw)
        assertEquals("NAVER WEBTOON", raw.companyName)
    }

    @Test
    fun `LG - companyName이 계열사명, 마감 날짜파싱`() {
        val n = LgJobNotice(
            jobNoticeId = 1001327,
            careerTypeName = "경력",
            companyName = "LG유플러스",
            jobNoticeName = "[CTO] GPU Software Engineer",
            recEndDateTime = "2026.07.05 23:00",
            jobGroupName = "연구/개발",
        )
        val raw = LgMapper.toRawJob(n)
        assertNotNull(raw)
        assertEquals("lg-1001327", raw.externalId)
        assertEquals("LG유플러스", raw.companyName)
        assertEquals("경력", raw.experience)
        assertEquals("연구/개발", raw.department)
        assertNotNull(raw.deadlineEpoch)
        assertEquals("https://careers.lg.com/app/careers/recruit/notice/detail/1001327", raw.originalUrl)
    }

    @Test
    fun `CJ - compnm이 계열사명, ms 날짜 변환, detail 원문`() {
        val j = CjJob(
            joNum = "8634",
            title = "올리브영 경력채용",
            company = "CJ올리브영",
            jobName = "Specialist",
            location = "서울",
            startDtMs = 1776006000000,
            endDtMs = null,
        )
        val raw = CjMapper.toRawJob(j)
        assertNotNull(raw)
        assertEquals("cj-8634", raw.externalId)
        assertEquals("CJ올리브영", raw.companyName)
        assertEquals("서울", raw.location)
        assertEquals("Specialist", raw.department)
        assertNotNull(raw.postingDateEpoch)
        assertNull(raw.deadlineEpoch)
        assertEquals("https://recruit.cj.net/recruit/ko/recruit/recruit/detail.fo?zz_jo_num=8634", raw.originalUrl)
    }

    @Test
    fun `필수값 없으면 null`() {
        assertNull(TossMapper.toRawJob(TossJobGroup(id = null)))
        assertNull(NaverMapper.toRawJob(NaverJob(annoId = 1, annoSubject = null)))
        assertNull(LgMapper.toRawJob(LgJobNotice(jobNoticeId = null)))
        assertNull(CjMapper.toRawJob(CjJob(joNum = null, title = "x")))
    }
}
