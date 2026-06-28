package com.jobalert.backend.client.source.lx

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * LX 목록 파서 — 2026-06-28 라이브 응답 fixture(src/test/resources/fixtures/lx_list.html) 기준.
 */
class LxListParserTest {

    private fun fixture(): String =
        javaClass.getResourceAsStream("/fixtures/lx_list.html")!!.bufferedReader().readText()

    @Test
    fun `fixture에서 계열사 공고가 파싱된다`() {
        val jobs = LxListParser.parse(fixture())
        assertTrue(jobs.isNotEmpty(), "공고가 파싱돼야 함")
        // 모든 공고: source=lx, 제목·외부ID·원문URL 존재
        jobs.forEach {
            assertEquals("lx", it.source)
            assertTrue(it.title.isNotBlank())
            assertTrue(it.externalId.startsWith("lx-"))
            assertTrue(it.originalUrl!!.contains("jobNoticeId="))
        }
        // 계열사명이 [LX...] 접두에서 추출됨
        val companies = jobs.map { it.companyName }.toSet()
        assertTrue(companies.any { it.startsWith("LX") }, "회사명 $companies")
        // 슬래시 뒤 채용유형은 회사명에서 제거 (예: "LX판토스/현지채용" → "LX판토스")
        assertTrue(companies.none { it.contains("/") }, "회사명에 슬래시 없어야: $companies")
    }

    @Test
    fun `제목에서 대괄호 접두는 제거된다`() {
        val jobs = LxListParser.parse(fixture())
        assertTrue(jobs.none { it.title.startsWith("[") }, "제목에 [계열사] 접두 남으면 안 됨")
        // 세미콘 경력 공고가 들어있는지(라이브 샘플 확인)
        val semicon = jobs.firstOrNull { it.companyName == "LX세미콘" }
        assertNotNull(semicon)
    }
}
