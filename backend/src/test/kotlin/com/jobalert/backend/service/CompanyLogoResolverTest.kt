package com.jobalert.backend.service

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CompanyLogoResolverTest {

    private val resolver = CompanyLogoResolver()

    @Test
    fun `homepageUrl이 있으면 우선 사용 (https + www 제거)`() {
        val url = resolver.resolveLogoUrl("아무회사", homepageUrl = "https://www.samsung.com/sec/")
        assertEquals("https://logo.clearbit.com/samsung.com", url)
    }

    @Test
    fun `homepageUrl이 스킴 없어도 처리`() {
        val url = resolver.resolveLogoUrl(null, homepageUrl = "kakao.com")
        assertEquals("https://logo.clearbit.com/kakao.com", url)
    }

    @Test
    fun `한글 회사명 dictionary lookup`() {
        assertEquals("https://logo.clearbit.com/samsung.com", resolver.resolveLogoUrl("삼성전자"))
        assertEquals("https://logo.clearbit.com/naver.com", resolver.resolveLogoUrl("네이버"))
        assertEquals("https://logo.clearbit.com/kakao.com", resolver.resolveLogoUrl("카카오"))
    }

    @Test
    fun `한글 회사명 dictionary는 정규화 변형도 흡수`() {
        // 정규화로 "카카오"가 되는 표기들이 동일한 domain으로 매핑
        assertEquals("https://logo.clearbit.com/kakao.com", resolver.resolveLogoUrl("(주)카카오"))
        assertEquals("https://logo.clearbit.com/kakao.com", resolver.resolveLogoUrl("㈜ 카카오"))
        assertEquals("https://logo.clearbit.com/kakao.com", resolver.resolveLogoUrl("주식회사 카카오"))
    }

    @Test
    fun `영문 회사명은 lowercase + 공백제거 + dotcom`() {
        assertEquals("https://logo.clearbit.com/foobar.com", resolver.resolveLogoUrl("Foo Bar"))
        assertEquals("https://logo.clearbit.com/airbnb.com", resolver.resolveLogoUrl("Airbnb"))
    }

    @Test
    fun `dictionary에 없는 한글명은 null`() {
        assertNull(resolver.resolveLogoUrl("처음보는한글회사"))
    }

    @Test
    fun `회사명이 null이고 homepageUrl도 없으면 null`() {
        assertNull(resolver.resolveLogoUrl(null, null))
        assertNull(resolver.resolveLogoUrl("", ""))
    }

    @Test
    fun `잘못된 homepageUrl이면 회사명 fallback`() {
        // URI 파싱 실패 → companyName 로직으로 fallback
        val url = resolver.resolveLogoUrl("Airbnb", homepageUrl = "ht!!tps://broken url ")
        assertEquals("https://logo.clearbit.com/airbnb.com", url)
    }

    @Test
    fun `resolveDomain 단독 호출 — URL 만들기 전 domain만 확인`() {
        assertEquals("samsung.com", resolver.resolveDomain("삼성전자"))
        assertEquals("naver.com", resolver.resolveDomain(null, "https://naver.com"))
        assertNull(resolver.resolveDomain("처음보는한글회사"))
    }
}
