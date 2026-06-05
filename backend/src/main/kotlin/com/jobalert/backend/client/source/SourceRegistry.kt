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

    /**
     * Greenhouse 보드. token = board token.
     * 아래는 모두 2026-06-05 실측으로 한국 공고 수 확인됨 (koreaOnly 필터 적용 후).
     */
    val greenhouseBoards: List<SourceBoard> = listOf(
        SourceBoard(token = "coupang", displayName = "쿠팡", homepage = "coupang.com"),     // 한국 267건
        SourceBoard(token = "krafton", displayName = "크래프톤", homepage = "krafton.com"),  // 한국 50건
        SourceBoard(token = "daangn", displayName = "당근마켓", homepage = "daangn.com"),     // 한국 40건
        SourceBoard(token = "moloco", displayName = "몰로코", homepage = "moloco.com"),       // 한국 20건
        SourceBoard(token = "sendbird", displayName = "센드버드", homepage = "sendbird.com"), // 한국 9건
        // TODO: 한국 회사 Greenhouse 토큰 추가 발굴 (확인법: 회사 채용페이지 URL이 job-boards.greenhouse.io/{token})
    )

    /**
     * Lever 보드. token = company slug.
     * TODO: 한국 회사 Lever slug 발굴 (확인법: jobs.lever.co/{slug}). 현재 확인된 한국 회사 없음.
     */
    val leverBoards: List<SourceBoard> = emptyList()
}
