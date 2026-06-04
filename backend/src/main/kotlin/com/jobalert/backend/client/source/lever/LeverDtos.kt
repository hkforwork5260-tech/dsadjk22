package com.jobalert.backend.client.source.lever

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Lever Postings API 응답 매핑.
 * 엔드포인트: GET https://api.lever.co/v0/postings/{company}?mode=json
 * 응답은 래퍼 없는 JSON 배열 → Array<LeverPosting>로 받는다.
 *
 * 인증 불필요(공개 조회). 실제 응답 필드 확인: 2026-06-04.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeverPosting(
    val id: String? = null,
    /** 공고 제목. */
    val text: String? = null,
    /** 등록 시각 (epoch millis). */
    val createdAt: Long? = null,
    val categories: LeverCategories? = null,
    /** 공개 공고 페이지 URL — 지원 이탈 링크. */
    val hostedUrl: String? = null,
    /** 지원 페이지. */
    val applyUrl: String? = null,
    /** 국가 코드 (예: "KR", "US"). */
    val country: String? = null,
    val workplaceType: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LeverCategories(
    val location: String? = null,
    val department: String? = null,
    val team: String? = null,
    val commitment: String? = null,
)
