package com.jobalert.backend.client.source.seoul

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 서울시 일자리포털 채용정보 API 응답 매핑.
 * 엔드포인트: GET http://openapi.seoul.go.kr:8088/{KEY}/json/GetJobInfo/{start}/{end}/
 * data.seoul.go.kr. 라이선스: 공공누리 1유형(상업 이용 OK). 실 응답 확인: 2026-06-06 (총 23,145건).
 *
 * 서울 소재 중소·중견 위주. 본문(DTY_CN)·급여(HOPE_WAGE)·마감일까지 제공.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SeoulJobResponse(
    @JsonProperty("GetJobInfo") val getJobInfo: SeoulJobBody? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeoulJobBody(
    @JsonProperty("list_total_count") val listTotalCount: Int? = null,
    @JsonProperty("RESULT") val result: SeoulResult? = null,
    val row: List<SeoulJob> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeoulResult(
    @JsonProperty("CODE") val code: String? = null,
    @JsonProperty("MESSAGE") val message: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeoulJob(
    @JsonProperty("JO_REQST_NO") val joReqstNo: String? = null,
    @JsonProperty("CMPNY_NM") val cmpnyNm: String? = null,
    @JsonProperty("JO_SJ") val joSj: String? = null,
    @JsonProperty("JOBCODE_NM") val jobcodeNm: String? = null,
    @JsonProperty("DTY_CN") val dtyCn: String? = null,
    @JsonProperty("ACDMCR_NM") val acdmcrNm: String? = null,
    @JsonProperty("CAREER_CND_NM") val careerCndNm: String? = null,
    @JsonProperty("HOPE_WAGE") val hopeWage: String? = null,
    @JsonProperty("WORK_PARAR_BASS_ADRES_CN") val workAddr: String? = null,
    @JsonProperty("BASS_ADRES_CN") val bassAddr: String? = null,
    @JsonProperty("RCEPT_CLOS_NM") val rceptClosNm: String? = null,
    @JsonProperty("JO_REG_DT") val joRegDt: String? = null,
    @JsonProperty("RCEPT_MTH_NM") val rceptMthNm: String? = null,
    @JsonProperty("PRESENTN_PAPERS_NM") val presentnPapersNm: String? = null,
    @JsonProperty("MNGR_PHON_NO") val mngrPhonNo: String? = null,
    @JsonProperty("EMPLYM_STLE_CMMN_MM") val emplymStle: String? = null,
)
