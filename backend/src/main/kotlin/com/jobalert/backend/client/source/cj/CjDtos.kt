package com.jobalert.backend.client.source.cj

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * CJ 그룹 통합 채용포털 응답 매핑.
 *
 * 엔드포인트: POST https://recruit.cj.net/recruit/ko/recruit/recruit/searchNewGonggoList.fo
 *   - 인증 불필요(form POST). 1콜로 CJ그룹 전체(제일제당·ENM·대한통운·올리브영 등) ~175건.
 *   - 실제 응답 확인: 2026-06-28. `ds_newRecruitList[]`, compnm이 계열사명으로 직접 옴.
 *   - 날짜는 epoch millis. zz_end_dt가 null이면 상시.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class CjResponse(
    @JsonProperty("ds_newRecruitList") val list: List<CjJob> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CjJob(
    @JsonProperty("zz_jo_num") val joNum: String? = null,
    @JsonProperty("zz_title") val title: String? = null,
    /** 계열사명(예: "CJ올리브영", "CJ제일제당"). */
    @JsonProperty("compnm") val company: String? = null,
    /** 직무명("Specialist" 등). */
    @JsonProperty("job_cd_nm") val jobName: String? = null,
    /** 근무지명("서울" 등). */
    @JsonProperty("location_cd_nm") val location: String? = null,
    /** 게시 시작(epoch millis). */
    @JsonProperty("zz_str_dt") val startDtMs: Long? = null,
    /** 마감(epoch millis). null이면 상시. */
    @JsonProperty("zz_end_dt") val endDtMs: Long? = null,
)
