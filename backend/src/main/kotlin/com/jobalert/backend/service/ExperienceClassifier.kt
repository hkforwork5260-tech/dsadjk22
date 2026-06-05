package com.jobalert.backend.service

import org.springframework.stereotype.Component

/**
 * 공고를 경력 버킷(신입/경력/인턴/무관)으로 분류.
 *
 * 1순위: 소스가 준 채용구분(공공기관 recrutSeNm) — 정확.
 * 2순위: 없으면(Greenhouse 등) 제목 키워드로 보충.
 * "신입+경력"처럼 둘 다면 신입으로(취준생 신입 필터에 잡히게).
 */
@Component
class ExperienceClassifier {

    private val yearPattern = Regex("""\d+\s*년""")

    fun classify(rawExperience: String?, title: String?): String {
        val hay = "${rawExperience ?: ""} ${title ?: ""}".lowercase()
        return when {
            hay.contains("인턴") || hay.contains("체험형") || hay.contains("intern") -> "인턴"
            hay.contains("신입") || hay.contains("신규채용") || hay.contains("new grad") -> "신입"
            hay.contains("경력") || hay.contains("senior") || hay.contains("경력직") ||
                yearPattern.containsMatchIn(hay) -> "경력"
            else -> "무관"
        }
    }
}
