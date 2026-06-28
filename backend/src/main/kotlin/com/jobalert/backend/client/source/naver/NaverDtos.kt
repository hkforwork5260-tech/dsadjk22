package com.jobalert.backend.client.source.naver

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * 네이버 채용 공개 API 응답 매핑.
 *
 * 엔드포인트: GET https://recruit.navercorp.com/rcrt/loadJobList.do?recordCountPerPage={n}
 *   - 인증 불필요. `list[]`에 NAVER + 계열사(네이버웹툰 등) 공고. 실제 응답 확인: 2026-06-28.
 *   - recordCountPerPage를 크게 주면 전체를 한 번에(현재 26건). jobDetailLink로 원문 링크 제공.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverResponse(
    val result: String? = null,
    val list: List<NaverJob> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverJob(
    val annoId: Long? = null,
    /** 회사명(예: "NAVER", "NAVER WEBTOON"). */
    val sysCompanyCdNm: String? = null,
    /** 공고 제목. */
    val annoSubject: String? = null,
    /** 채용구분명("경력"/"신입"). */
    val entTypeCdNm: String? = null,
    /** 게시 시작("yyyy.MM.dd HH:mm:ss"). */
    val staYmdTime: String? = null,
    /** 마감("yyyy.MM.dd HH:mm:ss"). */
    val endYmdTime: String? = null,
    /** 직군 대분류("Service & Business" 등). */
    val classCdNm: String? = null,
    /** 세부 직무("Content Development" 등). */
    val subJobCdNm: String? = null,
    /** 원문 상세 링크(전체 URL). */
    val jobDetailLink: String? = null,
)
