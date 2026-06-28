package com.jobalert.backend.client.source.greeting

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * 그리팅(두들린, greetinghr.com) 채용 공개 API 응답 매핑.
 *
 * 엔드포인트: GET https://api.greetinghr.com/ats/v1.1/career/workspaces/{workspaceId}/openings?page={0..}&pageSize={n}
 *   - 인증 불필요(공개 조회). X-Api-Version 헤더도 불필요(없어도 200).
 *   - 워크스페이스는 **경로의 workspaceId**로 지정(서브도메인 아님). 실제 응답 필드 확인: 2026-06-28.
 *   - `data.datas[]`가 공고 목록, `data.hasNext`로 페이지네이션.
 *
 * 무신사·컬리·HYBE·CJ올리브영·현대오토에버 등 한국 기업이 자체 채용페이지 ATS로 사용
 * ([com.jobalert.backend.client.source.SourceRegistry.greetingWorkspaces]).
 * 목록 API에 본문(description)은 없음 → 사실 메타데이터만 수집(어문저작권 무관, recruiter와 동일 이점).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingResponse(
    val success: Boolean = false,
    val data: GreetingData? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingData(
    val page: Int? = null,
    val pageSize: Int? = null,
    val totalCount: Int? = null,
    val totalPage: Int? = null,
    val hasNext: Boolean = false,
    val datas: List<GreetingOpening> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingOpening(
    val openingId: Long? = null,
    val title: String? = null,
    /** 게시 시작 ISO-8601 UTC(Z). 예 "2026-06-25T09:27:42Z". */
    val openDate: String? = null,
    /** 마감 ISO-8601 UTC(Z). null이면 상시. 예 "2026-06-30T14:59:59Z". */
    val dueDate: String? = null,
    /** 게시 여부(목록은 이미 게시건만 옴, 보통 true). */
    val deploy: Boolean = true,
    val group: GreetingGroup? = null,
    /** 사업부/계열사 구분(예: {division:"무신사"}). 없으면 null. */
    val workspaceDivision: GreetingDivision? = null,
    val openingJobPosition: GreetingJobPositionWrap? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingGroup(val name: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingDivision(val division: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingJobPositionWrap(
    val openingJobPositions: List<GreetingJobPosition> = emptyList(),
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingJobPosition(
    /** 직군 대분류(예: "IT", "마케팅"). 비어있을 수 있음. */
    val workspaceOccupation: GreetingOccupation? = null,
    /** 세부 직무(예: "Back-end Engineering"). */
    val workspaceJob: GreetingJob? = null,
    val workspacePlace: GreetingPlace? = null,
    val jobPositionCareer: GreetingCareer? = null,
    val jobPositionEmployment: GreetingEmployment? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingOccupation(val occupation: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingJob(val job: String? = null)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingPlace(
    /** 근무지 라벨(회사명이 들어오기도 함). */
    val location: String? = null,
    /** 전체 주소(예: "대한민국 서울특별시 용산구 한강대로 372"). */
    val place: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingCareer(
    /** NEW_COMER(신입)·EXPERIENCED(경력)·NOT_MATTER(무관). */
    val careerType: String? = null,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GreetingEmployment(
    /** FULL_TIME_WORKER·CONTRACT_WORKER·INTERN_WORKER. */
    val employmentType: String? = null,
)
