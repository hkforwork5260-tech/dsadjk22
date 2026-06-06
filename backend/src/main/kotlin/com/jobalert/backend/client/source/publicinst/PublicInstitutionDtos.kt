package com.jobalert.backend.client.source.publicinst

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * 기획재정부 공공기관 채용정보 조회서비스 응답 매핑.
 * 엔드포인트: GET https://apis.data.go.kr/1051000/recruitment/list
 * data.go.kr: https://www.data.go.kr/data/15125273/openapi.do
 *
 * 실 응답 필드 확인: 2026-06-05 (총 110,693건, resultCode 200).
 * 라이선스: 공공누리(이용허락 제한 없음), 무료, 개발계정 1,000회/일.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PublicInstitutionResponse(
    val resultCode: Int? = null,
    val resultMsg: String? = null,
    val totalCount: Int? = null,
    val result: List<PublicInstitutionJob> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PublicInstitutionJob(
    /** 공고 일련번호 (고유 ID). */
    val recrutPblntSn: Long? = null,
    /** 기관명. */
    val instNm: String? = null,
    /** 공고 제목. */
    val recrutPbancTtl: String? = null,
    /** 근무지역명 (콤마 구분 다중). */
    val workRgnNmLst: String? = null,
    /** NCS 직무명 (콤마 구분 다중). */
    val ncsCdNmLst: String? = null,
    /** 고용형태명 (정규직/비정규직 등). */
    val hireTypeNmLst: String? = null,
    /** 채용구분명 (신입/경력 등). */
    val recrutSeNm: String? = null,
    /** 공고 시작일 yyyyMMdd. */
    val pbancBgngYmd: String? = null,
    /** 공고 마감일 yyyyMMdd. */
    val pbancEndYmd: String? = null,
    /** 원본 공고 URL (기관 채용페이지). 지원 이탈 링크. */
    val srcUrl: String? = null,
    /** 진행중 여부 Y/N. */
    val ongoingYn: String? = null,
    /** 채용인원. */
    val recrutNope: Int? = null,
)

/**
 * 상세 조회 응답. 엔드포인트: GET .../recruitment/detail?sn={recrutPblntSn}
 * 목록(list)엔 없는 본문 텍스트(응시자격·전형방법·우대·학력)를 준다. 실 응답 확인: 2026-06-06.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class PublicInstitutionDetailResponse(
    val resultCode: Int? = null,
    val resultMsg: String? = null,
    val result: PublicInstitutionDetail? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PublicInstitutionDetail(
    val recrutPblntSn: Long? = null,
    /** 응시자격(본문). */
    val aplyQlfcCn: String? = null,
    /** 전형방법 설명(본문). */
    val scrnprcdrMthdExpln: String? = null,
    /** 결격사유. */
    val disqlfcRsn: String? = null,
    /** 우대사항. */
    val prefCn: String? = null,
    /** 학력 조건명 (예: "학력무관"). */
    val acbgCondNmLst: String? = null,
)
