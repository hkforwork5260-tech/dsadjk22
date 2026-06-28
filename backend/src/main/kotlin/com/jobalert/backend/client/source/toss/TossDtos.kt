package com.jobalert.backend.client.source.toss

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 토스(비바리퍼블리카) 공개 채용 API 응답 매핑.
 *
 * 엔드포인트: GET https://api-public.toss.im/api/v3/ipd-eggnog/career/job-groups
 *   - 인증 불필요. 내부적으로 Greenhouse 기반(absolute_url에 gh_jid). 실제 응답 확인: 2026-06-28.
 *   - `success[]`가 직무그룹, 각 그룹의 `primary_job`이 대표 공고. 토스 그룹사(토스뱅크·토스증권 등) 통합.
 *   - 소속 자회사·직군은 metadata 배열에서 추출(value_type=single_select).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class TossResponse(
    val resultType: String? = null,
    val success: List<TossJobGroup> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TossJobGroup(
    val id: Long? = null,
    val title: String? = null,
    @JsonProperty("primary_job") val primaryJob: TossPrimaryJob? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TossPrimaryJob(
    val title: String? = null,
    @JsonProperty("absolute_url") val absoluteUrl: String? = null,
    val location: TossLocation? = null,
    val metadata: List<TossMetadata> = emptyList(),
    /** 게시일 ISO-8601(offset 포함). 예 "2024-06-03T08:40:26-04:00". */
    @JsonProperty("first_published") val firstPublished: String? = null,
    /** 마감일 ISO-8601. null이면 상시. */
    @JsonProperty("application_deadline") val applicationDeadline: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TossLocation(val name: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TossMetadata(
    val name: String? = null,
    /** single_select면 String, 그 외 배열/객체일 수 있어 Any로 받고 매퍼에서 String만 사용. */
    val value: Any? = null,
)
