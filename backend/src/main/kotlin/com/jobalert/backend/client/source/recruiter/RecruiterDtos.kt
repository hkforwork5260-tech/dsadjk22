package com.jobalert.backend.client.source.recruiter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * recruiter.co.kr(MIDAS jobflex) 공개 API 응답 매핑.
 * 엔드포인트: POST https://api-recruiter.recruiter.co.kr/position/v1/jobflex
 *   헤더 `prefix: {tenant}.recruiter.co.kr`, body `{"pageableRq":{...},"filter":{}}`
 * 인증 불필요(공개 조회). 실제 응답 필드 확인: 2026-06-27.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RecruiterJobflexResponse(
    val pagination: RecruiterPagination? = null,
    val list: List<RecruiterPosition> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecruiterPagination(
    val page: Int? = null,
    val size: Int? = null,
    val totalCount: Int? = null,
    val totalPages: Int? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecruiterPosition(
    val positionSn: Long? = null,
    val title: String? = null,
    /** 게시 시작 ISO LocalDateTime(타임존 없음 = KST). 예 "2026-06-26T00:00:00". */
    val startDateTime: String? = null,
    /** 마감 ISO LocalDateTime(KST). 없으면 상시. */
    val endDateTime: String? = null,
    /** CAREER(경력)·NEW(신입)·NEW_CAREER(신입·경력)·INTERN 등. */
    val careerType: String? = null,
    /** 단일사면 경력구분("경력"), 그룹허브면 계열사명("만도브로제")으로 오버로드됨. */
    val classificationCode: String? = null,
    val dday: Int? = null,
    val tagList: List<RecruiterTag> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RecruiterTag(
    val tagSn: Long? = null,
    val tagName: String? = null,
)
