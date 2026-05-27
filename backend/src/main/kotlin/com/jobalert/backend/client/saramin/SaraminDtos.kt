package com.jobalert.backend.client.saramin

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 사람인 OpenAPI 호출 파라미터.
 * 공식 문서: https://oapi.saramin.co.kr/guide/job-search
 *
 * v0.1 수집 전략(A안): publishedMin=어제 + sr=directhire + sort=pd + 페이지네이션.
 * 회사 매칭은 백엔드에서 (사람인은 co_size 같은 파라미터 미지원).
 */
data class SaraminFetchParams(
    /** 등록일시 최소값(ISO datetime, 예: 2026-05-27T00:00:00). null이면 미지정. */
    val publishedMin: String? = null,
    /** 등록일시 최대값. */
    val publishedMax: String? = null,
    /** 자유검색 키워드. */
    val keywords: String? = null,
    /** 산업 코드(여러 개면 콤마 구분). */
    val indCd: String? = null,
    /** 제외 옵션(예: "directhire" → 헤드헌팅/파견업체 제외). */
    val sr: String? = null,
    /** 정렬: pd(등록일↓ 기본), pa(등록일↑), ud(수정일↓), da(마감일↑) 등. */
    val sort: String = "pd",
    /** 페이지 번호(0 기본). 사람인 API의 start는 0-based. */
    val start: Int = 0,
    /** 페이지 크기(10 기본, 110 최대). */
    val count: Int = 110,
    /** 추가 응답 필드(예: "posting-date,expiration-date,count"). */
    val fields: String? = null,

    // ─── v0.1 호환용 (legacy) ───
    /** @deprecated 사람인 API에 co_size 파라미터 없음. v0.5에서 indCd로 대체. */
    @Deprecated("사람인 API 미지원 파라미터. indCd 사용 권장.")
    val coSize: String? = null,
)

/**
 * 도메인 DTO — 백엔드 내부에서 다루는 정규화된 공고 형태.
 * raw response 매핑은 [SaraminApiResponse]를 거쳐 변환.
 */
data class SaraminJobDto(
    val externalId: String,
    val title: String,
    val companyName: String,
    val companyHomepage: String? = null,
    val industry: String? = null,
    val location: String? = null,
    val experience: String? = null,
    val education: String? = null,
    val salary: String? = null,
    val postingDateEpoch: Long? = null,
    val deadlineEpoch: Long? = null,
    val originalUrl: String? = null,
    val keywords: List<String> = emptyList(),
)

// ─── Raw response DTO (Jackson 매핑용) ─────────────────────────────────

/**
 * 사람인 OpenAPI 정상 응답 최상위.
 * 예: { "jobs": { "count": 110, "start": 0, "total": "1234", "job": [...] } }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminApiResponse(
    val jobs: SaraminJobsWrapper? = null,
    // 에러 응답일 경우 code/message가 최상위에 직접 옴.
    val code: Int? = null,
    val message: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminJobsWrapper(
    @JsonAlias("count") val count: Int? = null,
    @JsonAlias("start") val start: Int? = null,
    /** 사람인은 total을 string으로 반환. 안전하게 string으로 받고 toLong()으로 변환. */
    @JsonAlias("total") val total: String? = null,
    val job: List<SaraminJobRaw> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminJobRaw(
    val id: String? = null,
    val url: String? = null,
    /** "1" = 진행 중, "0" = 마감. */
    val active: String? = null,
    val company: SaraminCompanyWrapper? = null,
    val position: SaraminPosition? = null,
    val keyword: String? = null,
    val salary: SaraminCodeName? = null,
    @JsonProperty("posting-timestamp") val postingTimestamp: String? = null,
    @JsonProperty("posting-date") val postingDate: String? = null,
    @JsonProperty("modification-timestamp") val modificationTimestamp: String? = null,
    @JsonProperty("opening-timestamp") val openingTimestamp: String? = null,
    @JsonProperty("expiration-timestamp") val expirationTimestamp: String? = null,
    @JsonProperty("expiration-date") val expirationDate: String? = null,
    @JsonProperty("close-type") val closeType: SaraminCodeName? = null,
    @JsonProperty("read-cnt") val readCnt: String? = null,
    @JsonProperty("apply-cnt") val applyCnt: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminCompanyWrapper(
    val detail: SaraminCompanyDetail? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminCompanyDetail(
    val name: String? = null,
    val href: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminPosition(
    val title: String? = null,
    val industry: SaraminCodeName? = null,
    val location: SaraminCodeName? = null,
    @JsonProperty("job-type") val jobType: SaraminCodeName? = null,
    @JsonProperty("job-mid-code") val jobMidCode: SaraminCodeName? = null,
    @JsonProperty("job-code") val jobCode: SaraminCodeName? = null,
    @JsonProperty("experience-level") val experienceLevel: SaraminExperienceLevel? = null,
    @JsonProperty("required-education-level") val requiredEducationLevel: SaraminCodeName? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminCodeName(
    val code: String? = null,
    val name: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SaraminExperienceLevel(
    val code: String? = null,
    val min: String? = null,
    val max: String? = null,
    val name: String? = null,
)
