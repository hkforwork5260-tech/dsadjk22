package com.jobalert.backend.client.source

/**
 * 한 회사의 소스 보드 설정.
 *
 * Greenhouse면 token = board token (예: "databricks" → boards-api.greenhouse.io/v1/boards/databricks/jobs)
 * Lever면 token = company slug (예: "spotify" → api.lever.co/v0/postings/spotify)
 */
data class SourceBoard(
    /** Greenhouse board token 또는 Lever company slug. */
    val token: String,
    /** 앱에 노출할 회사명. */
    val displayName: String,
    /** 회사 홈페이지 (로고 리졸버·회사 매칭에 사용). */
    val homepage: String? = null,
    /**
     * 이 보드는 한국 공고만 필터링할지 여부.
     * 글로벌 빅테크 보드는 true(전 세계 중 한국만). 한국 전용 보드면 false로 전부 수집.
     */
    val koreaOnly: Boolean = true,
)

/**
 * recruiter.co.kr(MIDAS jobflex) 테넌트 설정.
 * prefix = `{tenant}.recruiter.co.kr` (jobflex API의 prefix 헤더 + 원문 링크 호스트).
 */
data class RecruiterTenant(
    /** tenant 식별자. 예: "kt" → prefix `kt.recruiter.co.kr`. */
    val tenant: String,
    /** 앱에 노출할 회사명(단일사). groupHub면 응답의 계열사명으로 덮어씀. */
    val displayName: String,
    /**
     * 그룹 통합 보드 여부. true면 응답 classificationCode가 계열사명(예 "만도브로제")이라
     * 그걸 회사명으로 쓴다(displayName은 그룹명 폴백). false면 classificationCode는 경력구분.
     */
    val groupHub: Boolean = false,
    /** 회사 홈페이지(로고·매칭). 없으면 null. */
    val homepage: String? = null,
)

/**
 * 그리팅(greetinghr.com) 워크스페이스 설정.
 * API는 경로 workspaceId로 공고를 조회하고, 원문 링크는 서브도메인을 쓴다(둘이 별개라 따로 보관).
 *   - openings API: api.greetinghr.com/ats/v1.1/career/workspaces/{workspaceId}/openings
 *   - 원문 링크: https://{subdomain}.career.greetinghr.com/o/{openingId}
 */
data class GreetingWorkspace(
    /** 그리팅 서브도메인. 예: "oliveyoung" → oliveyoung.career.greetinghr.com (원문 링크 호스트). */
    val subdomain: String,
    /** openings API 경로의 워크스페이스 ID(서브도메인과 값이 다를 수 있음 — 직접 검증 필수). */
    val workspaceId: Long,
    /** 앱에 노출할 회사명(응답 group.name은 "컬리 채용(Kurly Talent)" 등 노이즈가 많아 이 값을 우선). */
    val displayName: String,
    /** 회사 홈페이지(로고·매칭). 없으면 null. */
    val homepage: String? = null,
    /**
     * 원문 링크용 커스텀 도메인. 그리팅 서브도메인 대신 회사 자체 채용 도메인을 쓰는 경우만 지정
     * (예: JYP → "recruit.jype.com"). null이면 `{subdomain}.career.greetinghr.com`를 사용.
     */
    val originHost: String? = null,
)
