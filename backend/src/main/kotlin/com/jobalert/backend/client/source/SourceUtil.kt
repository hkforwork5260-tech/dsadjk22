package com.jobalert.backend.client.source

import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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

    /**
     * 공고 본문 HTML → 평문. Greenhouse content는 HTML이 엔티티 인코딩돼 오므로
     * (예: `&lt;div&gt;`) 먼저 디코드한 뒤 태그를 제거한다. 비거나 정제 후 빈 문자열이면 null.
     */
    fun htmlToText(html: String?): String? {
        if (html.isNullOrBlank()) return null
        val unescaped = Parser.unescapeEntities(html, false)
        val text = Jsoup.parse(unescaped).text().trim()
        return text.takeIf { it.isNotBlank() }
    }

    private val ZONE_KST = ZoneId.of("Asia/Seoul")
    private val YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd")

    /**
     * "yyyyMMdd"(예: "20260619")을 KST 기준 epoch seconds로.
     * @param endOfDay true면 그날 23:59:59(마감일용), false면 00:00:00(등록일용).
     * 파싱 실패 시 null.
     */
    fun yyyymmddToEpochSeconds(s: String?, endOfDay: Boolean = false): Long? {
        if (s.isNullOrBlank()) return null
        return try {
            val date = LocalDate.parse(s.trim(), YYYYMMDD)
            val dateTime = if (endOfDay) date.atTime(23, 59, 59) else date.atStartOfDay()
            dateTime.atZone(ZONE_KST).toEpochSecond()
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
