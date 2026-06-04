package com.jobalert.backend.client.source

import java.time.OffsetDateTime
import java.time.format.DateTimeParseException

/**
 * 소스 공통 유틸 — 한국 공고 필터링 + 시간 정규화.
 *
 * 글로벌 빅테크 보드(Greenhouse/Lever)는 전 세계 공고를 다 주므로,
 * 한국 근무지 공고만 골라내야 한다(타겟: 한국 취준생).
 */
object SourceUtil {

    /** 근무지 문자열에 이게 하나라도 들어가면 한국 공고로 간주. */
    private val KOREA_LOCATION_MARKERS = listOf(
        "korea", "seoul", "서울", "한국", "대한민국", "republic of korea", "korea, republic of",
    )

    /**
     * 근무지 문자열이 한국으로 보이는가.
     * @param location 예: "Seoul, Korea", "Remote - Korea", "서울"
     */
    fun isKoreaLocation(location: String?): Boolean {
        if (location.isNullOrBlank()) return false
        val lower = location.lowercase()
        return KOREA_LOCATION_MARKERS.any { lower.contains(it) }
    }

    /**
     * ISO-8601 문자열(예: "2026-04-09T16:51:29-04:00")을 epoch seconds(UTC)로.
     * 파싱 실패하면 null.
     */
    fun isoToEpochSeconds(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        return try {
            OffsetDateTime.parse(iso).toEpochSecond()
        } catch (_: DateTimeParseException) {
            null
        }
    }

    /** epoch millis → epoch seconds. null·0 이하는 null. */
    fun millisToEpochSeconds(millis: Long?): Long? =
        if (millis == null || millis <= 0) null else millis / 1000
}
