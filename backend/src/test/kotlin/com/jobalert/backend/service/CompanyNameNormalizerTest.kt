package com.jobalert.backend.service

import kotlin.test.Test
import kotlin.test.assertEquals

class CompanyNameNormalizerTest {

    @Test
    fun `(주) 접두 제거`() {
        assertEquals("카카오", CompanyNameNormalizer.normalize("(주)카카오"))
        assertEquals("카카오", CompanyNameNormalizer.normalize("(주) 카카오"))
    }

    @Test
    fun `㈜ 기호 접두 제거`() {
        assertEquals("네이버", CompanyNameNormalizer.normalize("㈜네이버"))
    }

    @Test
    fun `주식회사 접두 제거`() {
        assertEquals("삼성전자", CompanyNameNormalizer.normalize("주식회사 삼성전자"))
        assertEquals("삼성전자", CompanyNameNormalizer.normalize("삼성전자 주식회사"))
    }

    @Test
    fun `여러 마커 중첩 제거 (안정 상태까지 반복)`() {
        assertEquals("카카오", CompanyNameNormalizer.normalize("(주) 주식회사 카카오"))
        assertEquals("카카오", CompanyNameNormalizer.normalize("주식회사 (주)카카오"))
    }

    @Test
    fun `공백·중점·점·특수문자 제거`() {
        assertEquals("lg에너지솔루션", CompanyNameNormalizer.normalize("LG 에너지 솔루션"))
        assertEquals("sk하이닉스", CompanyNameNormalizer.normalize("SK·하이닉스"))
        // &는 단순 제거. "KT&G" 같은 표기는 alias 테이블에서 별도 매칭 (KT앤지·KTNG 등).
        assertEquals("ktg", CompanyNameNormalizer.normalize("KT&G"))
    }

    @Test
    fun `영문 소문자화`() {
        assertEquals("kakao", CompanyNameNormalizer.normalize("KAKAO"))
        assertEquals("naver", CompanyNameNormalizer.normalize("Naver"))
    }

    @Test
    fun `Corp Inc Ltd 등 영문 접미 제거`() {
        assertEquals("samsung", CompanyNameNormalizer.normalize("Samsung Corp."))
        assertEquals("kakao", CompanyNameNormalizer.normalize("Kakao Inc"))
        assertEquals("naver", CompanyNameNormalizer.normalize("NAVER Ltd."))
    }

    @Test
    fun `한국 표기와 영문 표기 매칭 (같은 회사면 다른 정규화 결과 가능 — alias로 보완)`() {
        // "삼성전자" vs "Samsung Electronics" 는 alias 테이블로 매칭한다.
        // normalize 자체로는 다른 결과가 나오는 게 정상.
        val ko = CompanyNameNormalizer.normalize("삼성전자")
        val en = CompanyNameNormalizer.normalize("Samsung Electronics")
        assertEquals("삼성전자", ko)
        assertEquals("samsungelectronics", en)
    }

    @Test
    fun `null·빈문자열·공백만은 빈 문자열 반환`() {
        assertEquals("", CompanyNameNormalizer.normalize(null))
        assertEquals("", CompanyNameNormalizer.normalize(""))
        assertEquals("", CompanyNameNormalizer.normalize("   "))
    }

    @Test
    fun `전각 영문 NFKC 정규화 후 반각`() {
        // 전각 K, T → 반각 k, t
        assertEquals("kt", CompanyNameNormalizer.normalize("ＫＴ"))
    }

    @Test
    fun `같은 회사의 다양한 표기가 같은 결과`() {
        val canonical = CompanyNameNormalizer.normalize("카카오")
        listOf(
            "(주)카카오",
            "㈜카카오",
            "주식회사 카카오",
            " 카카오 ",
            "카카오 주식회사",
        ).forEach {
            assertEquals(canonical, CompanyNameNormalizer.normalize(it), "원본='$it'")
        }
    }
}
