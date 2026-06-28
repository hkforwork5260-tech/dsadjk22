package com.jobalert.backend.client.source.lg

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * LG 통합 채용 API 응답 매핑.
 *
 * 엔드포인트: POST https://api.careers.lg.com/rmk/job/retrieveJobNoticesList
 *   - 인증 불필요. 빈 필터 바디 1콜로 LG그룹 전체(전자·화학·CNS·에너지솔루션·이노텍·디스플레이·
 *     유플러스·생활건강·Magna 등 14개 계열) ~110건. 실제 응답 확인: 2026-06-28.
 *   - `data.jobNoticeList[]`. companyName이 계열사명으로 직접 옴(레지스트리 불필요).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class LgResponse(
    val status: String? = null,
    val data: LgData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LgData(
    val jobNoticeList: List<LgJobNotice> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class LgJobNotice(
    val jobNoticeId: Long? = null,
    /** 채용구분명("경력"/"신입"/"인턴"/"신입/경력"/"산학장학생"). */
    val careerTypeName: String? = null,
    /** 계열사명(예: "LG전자", "LG화학", "LG CNS"). */
    val companyName: String? = null,
    /** 공고 제목. */
    val jobNoticeName: String? = null,
    /** 마감 일시("yyyy.MM.dd HH:mm"). 없으면 상시. */
    val recEndDateTime: String? = null,
    /** 게시 상태("POSTING" 등). */
    val noticeStatus: String? = null,
    /** 직군명("연구/개발" 등). */
    val jobGroupName: String? = null,
)
