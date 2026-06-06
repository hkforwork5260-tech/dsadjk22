package com.jobalert.backend.client.source

/**
 * 소스 무관 정규화 공고 모델.
 *
 * 하이브리드 수집기의 핵심: Greenhouse·Lever·(향후)그리팅 등 어디서 왔든
 * 모든 [JobSource]는 이 공통 타입으로 변환해서 뱉는다. 그래야 diff·정규화·푸시
 * 파이프라인이 소스 종류를 몰라도 그대로 동작한다.
 *
 * 사람인 단독 시절의 `SaraminJobDto`를 소스 무관 형태로 승격한 것.
 * (사람인도 폐기가 아니라 한 소스로 격하 — 향후 `SaraminJobDto` → RawJobPosting 어댑터로 합류 가능)
 *
 * 시간 필드 규약: **epoch SECONDS (UTC)**. 소스마다 ms/ISO 문자열로 오는 걸 초 단위로 통일.
 */
data class RawJobPosting(
    /** 출처 소스 식별자. 예: "greenhouse", "lever", "saramin". 출처 표기·dedup 키 prefix에 사용. */
    val source: String,
    /** 전역 유일 ID. 관례: "{source}-{board}-{id}". diff·upsert 키. */
    val externalId: String,
    val title: String,
    val companyName: String,
    val companyHomepage: String? = null,
    /** 근무지. 예: "Seoul, Korea". 한국 공고 필터링에 사용. */
    val location: String? = null,
    /** 직군/부서/팀. 소스에 따라 있을 수도 없을 수도. */
    val department: String? = null,
    /** 채용구분 원시값(공공기관 recrutSeNm 등: "신입"/"경력"/"인턴"). 없으면 제목에서 추출. */
    val experience: String? = null,
    /** 등록일시 (epoch seconds, UTC). 없으면 null. */
    val postingDateEpoch: Long? = null,
    /** 마감일시 (epoch seconds, UTC). Greenhouse·Lever는 마감 개념이 없어 대개 null. */
    val deadlineEpoch: Long? = null,
    /** 원본 공고 URL — 지원은 여기로 보낸다(이탈). ③ 메타데이터 수집의 핵심 필드. */
    val originalUrl: String? = null,
    val keywords: List<String> = emptyList(),
    /** 공고 본문(평문). 소스가 본문을 주면 HTML 정제 후 채운다(Greenhouse content 등). 없으면 null. */
    val description: String? = null,
    /** 학력 조건. 공공기관 상세(acbgCondNmLst: "학력무관" 등). 없으면 null. */
    val education: String? = null,
)
