package com.jobalert.backend.client.source.samsung

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 삼성 채용 `list.data`의 **실제 응답 HTML 조각**(2026-06-27 라이브 호출본 발췌)을
 * 파서가 제대로 읽어내는지 검증. CSS 셀렉터 오타·구조 변경 조기 발견.
 */
class SamsungListParserTest {

    private val baseUrl = "https://www.samsungcareers.com/hr"

    // list.data 실 응답 발췌 (li 2건 + 총건수 input)
    private val realSample = """
        <input type="hidden" class="divCnt" data-value="7" data-max="1">
        <li>
            <div><div>
                <div class="btnWrap">
                    <button type="button" class="btnShare" data-value="22,584"><i>공유</i></button>
                    <button type="button" class="btnScrap" data-value="18,456"><i>스크랩</i></button>
                </div>
                <a href="/#none" data-value="22,584">
                    <p class="company">  삼성전기</p>
                    <h3 class="title">경력사원 채용(패키지 부문) </h3>
                    <p class="info">
                        <span> 경력 </span>
                        <span class="period">2026.06.26 ~ 2026.07.13</span>
                    </p>
                </a>
            </div>
            <div class="flagWrap">
                <span class="flag blue">D- 16</span>
                <span class="flag grey">제품개발</span>
                <span class="flag grey">공정개발</span>
                <span class="flag grey">품질</span>
            </div></div>
        </li>
        <li>
            <div><div>
                <div class="btnWrap">
                    <button type="button" class="btnShare" data-value="22,542"><i>공유</i></button>
                </div>
                <a href="/#none" data-value="22,542">
                    <p class="company">  제일기획</p>
                    <h3 class="title">경력사원 채용(데이터, 스페이스 기획) </h3>
                    <p class="info">
                        <span> 경력 </span>
                        <span class="period">2026.06.23 ~ 2026.06.29</span>
                    </p>
                </a>
            </div>
            <div class="flagWrap">
                <span class="flag blue">D- 2</span>
                <span class="flag grey">CRM 전략/기획</span>
            </div></div>
        </li>
    """.trimIndent()

    @Test
    fun `실 HTML을 RawJobPosting으로 파싱한다`() {
        val (jobs, total) = SamsungListParser.parse(realSample, baseUrl)

        assertEquals(7, total, "총건수(input.divCnt)")
        assertEquals(2, jobs.size, "li 2건")

        val first = jobs[0]
        assertEquals("samsung", first.source)
        assertEquals("samsung-22584", first.externalId, "콤마 제거된 공고번호")
        assertEquals("경력사원 채용(패키지 부문)", first.title)
        assertEquals("삼성전기", first.companyName, "앞 공백 trim")
        assertEquals("경력", first.experience)
        assertEquals("https://www.samsungcareers.com/hr/?no=22584", first.originalUrl)
        assertEquals(listOf("제품개발", "공정개발", "품질"), first.tags, "grey 플래그=직무태그")
        assertEquals(first.tags, first.keywords, "분류기 입력에도 동일")
        assertNotNull(first.postingDateEpoch)
        assertNotNull(first.deadlineEpoch)
        // 마감(2026.07.13 23:59:59 KST)이 등록(2026.06.26)보다 뒤.
        assertTrue(first.deadlineEpoch!! > first.postingDateEpoch!!)

        assertEquals("제일기획", jobs[1].companyName, "계열사가 섞여 나옴")
    }

    @Test
    fun `공고번호 없는 li는 스킵한다`() {
        val noise = """
            <input class="divCnt" data-value="0">
            <li><div>광고/안내 영역(공고 아님)</div></li>
        """.trimIndent()
        val (jobs, total) = SamsungListParser.parse(noise, baseUrl)
        assertEquals(0, total)
        assertTrue(jobs.isEmpty())
    }
}
