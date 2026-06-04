package com.jobalert.backend.client.source

/**
 * 채용 공고 소스의 공통 인터페이스 (소스 어댑터 패턴).
 *
 * 구현체: [com.jobalert.backend.client.source.greenhouse.GreenhouseSource],
 *        [com.jobalert.backend.client.source.lever.LeverSource], (향후) GreetingSource 등.
 *
 * 수집기([com.jobalert.backend.service.JobCollectorService])는 등록된 모든 JobSource를
 * 순회하며 [fetchAll]을 호출하고, 결과 [RawJobPosting] 리스트를 합쳐 diff·정규화로 넘긴다.
 *
 * 규약:
 *  - 한 소스 내부에서 회사 하나가 실패해도 전체를 죽이지 말 것 (개별 try/catch, 로그 후 계속).
 *  - 네트워크 예외는 던지지 말고 빈 리스트로 흡수하거나 부분 결과 반환 (수집은 best-effort).
 */
interface JobSource {
    /** 소스 식별자. [RawJobPosting.source]와 일치. 예: "greenhouse". */
    val sourceId: String

    /** 이 소스가 커버하는 모든 회사의 공고를 가져온다. */
    fun fetchAll(): List<RawJobPosting>
}
