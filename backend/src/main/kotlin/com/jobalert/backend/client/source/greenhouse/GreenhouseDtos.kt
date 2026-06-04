package com.jobalert.backend.client.source.greenhouse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Greenhouse Job Board API 응답 매핑.
 * 엔드포인트: GET https://boards-api.greenhouse.io/v1/boards/{token}/jobs?content=false
 * 공식 문서: https://developers.greenhouse.io/job-board.html
 *
 * 인증 불필요(공개 조회). 실제 응답 필드 확인: 2026-06-04.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GreenhouseJobsResponse(
    val jobs: List<GreenhouseJob> = emptyList(),
    val meta: GreenhouseMeta? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreenhouseMeta(
    val total: Int? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreenhouseJob(
    val id: Long? = null,
    val title: String? = null,
    /** 회사명 (보드에 설정돼 있으면). */
    val company_name: String? = null,
    val location: GreenhouseLocation? = null,
    /** 공개 공고 페이지 URL — 지원 이탈 링크. */
    val absolute_url: String? = null,
    /** 최초 게시 ISO datetime. */
    val first_published: String? = null,
    /** 최종 수정 ISO datetime. */
    val updated_at: String? = null,
    /** 지원 마감 ISO datetime (있을 수도 없을 수도). */
    val application_deadline: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreenhouseLocation(
    val name: String? = null,
)
