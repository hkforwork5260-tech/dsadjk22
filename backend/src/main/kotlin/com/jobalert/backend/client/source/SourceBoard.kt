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
