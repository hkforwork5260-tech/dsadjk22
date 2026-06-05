package com.jobalert.backend.client.source

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SourceUtilTest {

    @Test
    fun `한국 근무지 문자열을 인식한다`() {
        assertTrue(SourceUtil.isKoreaLocation("Seoul, Korea"))
        assertTrue(SourceUtil.isKoreaLocation("서울"))
        assertTrue(SourceUtil.isKoreaLocation("Remote - South Korea"))
        assertTrue(SourceUtil.isKoreaLocation("KOREA, REPUBLIC OF"))
        assertTrue(SourceUtil.isKoreaLocation("대한민국 경기"))
    }

    @Test
    fun `한국이 아닌 근무지는 거른다`() {
        assertFalse(SourceUtil.isKoreaLocation("Tokyo, Japan"))
        assertFalse(SourceUtil.isKoreaLocation("San Francisco, CA"))
        assertFalse(SourceUtil.isKoreaLocation("Remote - US"))
        assertFalse(SourceUtil.isKoreaLocation(null))
        assertFalse(SourceUtil.isKoreaLocation(""))
    }

    @Test
    fun `ISO datetime을 epoch seconds로 변환한다`() {
        // 2026-04-09T16:51:29-04:00 == 2026-04-09T20:51:29Z == 1775767889
        assertEquals(1775767889L, SourceUtil.isoToEpochSeconds("2026-04-09T16:51:29-04:00"))
        assertEquals(1775767889L, SourceUtil.isoToEpochSeconds("2026-04-09T20:51:29+00:00"))
    }

    @Test
    fun `잘못된 ISO 문자열은 null`() {
        assertNull(SourceUtil.isoToEpochSeconds(null))
        assertNull(SourceUtil.isoToEpochSeconds(""))
        assertNull(SourceUtil.isoToEpochSeconds("not-a-date"))
        assertNull(SourceUtil.isoToEpochSeconds("2026-04-09")) // 시각 없음 → OffsetDateTime 파싱 실패
    }

    @Test
    fun `yyyyMMdd를 KST epoch seconds로 변환한다`() {
        // 2026-06-19 00:00:00 KST == 2026-06-18 15:00:00 UTC == 1781794800
        assertEquals(1781794800L, SourceUtil.yyyymmddToEpochSeconds("20260619"))
        // endOfDay=true → 23:59:59 KST == +86399초
        assertEquals(1781794800L + 86399L, SourceUtil.yyyymmddToEpochSeconds("20260619", endOfDay = true))
    }

    @Test
    fun `잘못된 yyyyMMdd는 null`() {
        assertNull(SourceUtil.yyyymmddToEpochSeconds(null))
        assertNull(SourceUtil.yyyymmddToEpochSeconds(""))
        assertNull(SourceUtil.yyyymmddToEpochSeconds("2026-06-19"))
        assertNull(SourceUtil.yyyymmddToEpochSeconds("notadate"))
    }

    @Test
    fun `epoch millis를 seconds로 변환한다`() {
        // Lever createdAt 예시: 1778529611285 ms → 1778529611 s
        assertEquals(1778529611L, SourceUtil.millisToEpochSeconds(1778529611285L))
        assertNull(SourceUtil.millisToEpochSeconds(null))
        assertNull(SourceUtil.millisToEpochSeconds(0))
        assertNull(SourceUtil.millisToEpochSeconds(-5))
    }
}
