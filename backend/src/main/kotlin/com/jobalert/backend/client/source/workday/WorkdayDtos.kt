package com.jobalert.backend.client.source.workday

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Workday CXS 채용 API 응답 매핑.
 *
 * 엔드포인트: POST https://{host}.myworkdayjobs.com/wday/cxs/{cxsTenant}/{site}/jobs
 *   - 인증 불필요. body `{"limit":20,"offset":0,"searchText":""}` 페이지네이션(offset+limit, total).
 *   - 실제 응답 확인: 2026-06-28(대웅제약). `jobPostings[]`, postedOn은 상대표현이라 날짜 미사용.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkdayResponse(
    val total: Int? = null,
    val jobPostings: List<WorkdayJobPosting> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkdayJobPosting(
    val title: String? = null,
    /** 상세 경로(원문 링크에 site와 합쳐 사용). 예: "/job/KOR-Daejeon/...". */
    val externalPath: String? = null,
    /** 근무지 텍스트. 예: "KOR-Daejeon". */
    val locationsText: String? = null,
    /** 고용형태("Full time" 등). */
    val timeType: String? = null,
    /** 요청번호 등(첫 값을 외부ID로 사용). 예: ["R-26-1815"]. */
    val bulletFields: List<String> = emptyList(),
)
