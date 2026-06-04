package com.jobalert.backend.client.source

import org.springframework.stereotype.Component

/**
 * 수집 대상 보드 목록 (소스별).
 *
 * ⚠️ v0.1 시드 상태: 아래는 **공개 API 동작이 실제 검증된 예시**일 뿐, 한국지사 채용을 많이
 *    올리는 회사로 큐레이션된 목록이 아니다. 진짜 운용하려면 "한국 오피스를 둔 글로벌 빅테크 중
 *    Greenhouse/Lever를 쓰는 회사"의 토큰을 리서치해서 채워야 한다. (별도 태스크)
 *    - Greenhouse 토큰 확인법: 회사 채용페이지에서 boards.greenhouse.io/{token} 또는
 *      job-boards.greenhouse.io/{token} URL 확인
 *    - Lever slug 확인법: jobs.lever.co/{slug}
 *
 * koreaOnly=true면 [SourceUtil.isKoreaLocation] 필터로 한국 근무지 공고만 남긴다.
 * 향후 application.yml 바인딩(@ConfigurationProperties)으로 빼서 코드 수정 없이 회사 추가 가능.
 */
@Component
class SourceRegistry {

    /** Greenhouse 보드. token = board token. */
    val greenhouseBoards: List<SourceBoard> = listOf(
        // 동작 검증됨(2026-06-04): boards-api.greenhouse.io/v1/boards/databricks/jobs → 747건
        SourceBoard(token = "databricks", displayName = "Databricks", homepage = "databricks.com"),
        // TODO(리서치): 한국 오피스 글로벌 빅테크 Greenhouse 토큰 채우기
    )

    /** Lever 보드. token = company slug. */
    val leverBoards: List<SourceBoard> = listOf(
        // 동작 검증됨(2026-06-04): api.lever.co/v0/postings/spotify → 168건
        SourceBoard(token = "spotify", displayName = "Spotify", homepage = "spotify.com"),
        // TODO(리서치): 한국 오피스 글로벌 빅테크 Lever slug 채우기
    )
}
