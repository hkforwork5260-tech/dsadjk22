package com.jobalert.backend.service

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class JobCategoryClassifierTest {

    private val classifier = JobCategoryClassifier()

    @Test
    fun `간호사 공고는 medical`() {
        val codes = classifier.classify("계약직 간호사(간호부-소화기내과) 재공고")
        assertContains(codes, "medical")
    }

    @Test
    fun `백엔드 개발자는 it_dev_data`() {
        val codes = classifier.classify("신입 백엔드 개발자", department = "Engineering")
        assertContains(codes, "it_dev_data")
    }

    @Test
    fun `연구개발 공고는 research`() {
        assertContains(classifier.classify("연구개발(R&D) 신입연구원"), "research")
    }

    @Test
    fun `부서·키워드도 매칭 근거에 포함`() {
        // 제목엔 직군 단서가 없지만 부서(NCS 분류명)에 단서
        val codes = classifier.classify("2026년 상반기 채용", department = "보건의료")
        assertContains(codes, "medical")
    }

    @Test
    fun `복수 직군 동시 부여 가능`() {
        val codes = classifier.classify("AI 연구개발 엔지니어")
        assertTrue(codes.containsAll(listOf("it_dev_data", "research")), "실제=$codes")
    }

    @Test
    fun `단서 없으면 빈 리스트(미분류)`() {
        assertEquals(emptyList(), classifier.classify("ㅁㄴㅇㄹ 자스러운 무언가"))
        assertEquals(emptyList(), classifier.classify(null))
    }
}
