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

    /**
     * recruiter.co.kr(MIDAS jobflex) 테넌트. prefix = `{tenant}.recruiter.co.kr`.
     * 한국 대기업이 가장 많이 쓰는 공유 ATS — 한 어댑터가 prefix만 바꿔 다사 커버.
     * 아래는 2026-06-27 상위 200개사 정찰에서 **신형 jobflex API가 200+공고로 실동작 확인된 tenant**만.
     * (구버전/career 제품은 jobflex가 `NotFoundPostedDesignException`을 줘서 제외 — 별도 파서 필요.)
     * groupHub=true면 응답 classificationCode가 계열사명(예 hlcompany→"만도브로제") → 회사명으로 사용.
     */
    val recruiterTenants: List<RecruiterTenant> = listOf(
        // 통신·전자
        RecruiterTenant("kt", "KT"),
        RecruiterTenant("wonik", "원익", groupHub = true),
        RecruiterTenant("dbgroup", "DB그룹", groupHub = true),       // DB하이텍·DB손해보험 등
        // 현대차그룹 부품·계열
        RecruiterTenant("mobis", "현대모비스"),
        RecruiterTenant("hyundai-wia", "현대위아"),
        RecruiterTenant("hyundai-transys", "현대트랜시스"),
        RecruiterTenant("hyundai-kefico", "현대케피코"),
        RecruiterTenant("glovis", "현대글로비스"),
        RecruiterTenant("hyundai-steel", "현대제철"),
        RecruiterTenant("hyundai-rotem", "현대로템"),
        RecruiterTenant("careerhyundai", "현대카드", groupHub = true), // 현대카드·현대커머셜
        // 자동차 부품·타이어
        RecruiterTenant("hanonsystems", "한온시스템"),
        RecruiterTenant("hankooktire", "한국타이어"),
        RecruiterTenant("nexentire", "넥센타이어"),
        RecruiterTenant("kumhotire", "금호타이어"),
        RecruiterTenant("kgm", "KG모빌리티"),
        RecruiterTenant("slworld", "에스엘"),
        RecruiterTenant("swhitech", "성우하이텍"),
        RecruiterTenant("hlcompany", "HL그룹", groupHub = true),     // HL홀딩스·만도·만도브로제 등
        // GS·LS
        RecruiterTenant("gsretail", "GS리테일"),
        RecruiterTenant("gsenc", "GS건설"),
        RecruiterTenant("gsenergy", "GS에너지"),
        RecruiterTenant("lselectric", "LS일렉트릭"),
        // 화학·에너지
        RecruiterTenant("s-oil", "에쓰오일"),
        RecruiterTenant("ecoprogroup", "에코프로", groupHub = true),
        // 건설
        RecruiterTenant("secc", "삼성물산 건설부문"),
        RecruiterTenant("hec", "현대엔지니어링"),
        RecruiterTenant("dlenc", "DL이앤씨"),
        // 금융
        RecruiterTenant("shinhan", "신한은행"),
        RecruiterTenant("hanabank", "하나은행"),
        RecruiterTenant("kbsec", "KB증권"),
        RecruiterTenant("nhqv", "NH투자증권"),
        RecruiterTenant("hi", "현대해상"),
        RecruiterTenant("meritz", "메리츠증권"),
        // 유통·식음료·패션
        RecruiterTenant("spc", "SPC", groupHub = true),
        RecruiterTenant("fnf", "F&F"),
        RecruiterTenant("orion", "오리온"),
        RecruiterTenant("maeil", "매일유업"),
        RecruiterTenant("shinsegaeinc", "신세계I&C"),
        // 제약·바이오
        RecruiterTenant("celltrion", "셀트리온"),
        RecruiterTenant("yuhan", "유한양행"),
        RecruiterTenant("hanmi", "한미약품"),
        RecruiterTenant("kolmar", "한국콜마", groupHub = true),      // HK이노엔 등
        // 항공·물류
        RecruiterTenant("koreanair", "대한항공"),
        RecruiterTenant("jejuair", "제주항공"),
        RecruiterTenant("twayair", "티웨이항공"),
        // 게임·엔터
        RecruiterTenant("com2us", "컴투스"),
        RecruiterTenant("webzen", "웹젠"),
        RecruiterTenant("yg-entertainment", "YG엔터테인먼트"),
    )

    /**
     * 그리팅(greetinghr.com) 워크스페이스. 인증 불필요 공개 API.
     * 아래는 2026-06-28 라이브에서 **openings API가 200+공고로 실동작 확인된 워크스페이스**만.
     * (workspaceId는 직접 검증함 — 서브도메인과 값이 다를 수 있어 추측 금지.)
     * 더 추가하려면: `{subdomain}.career.greetinghr.com/ko/home`의 임베드 JSON에서 workspaceId를
     *   뽑아 openings API로 검증 후 등록. group.name은 노이즈가 많아 displayName을 직접 지정.
     */
    val greetingWorkspaces: List<GreetingWorkspace> = listOf(
        GreetingWorkspace("oliveyoung", 10501, "CJ올리브영", homepage = "oliveyoung.co.kr"),
        GreetingWorkspace("hybe", 10002, "HYBE", homepage = "hybecorp.com"),
        GreetingWorkspace("musinsa", 1455, "무신사", homepage = "musinsa.com"),
        GreetingWorkspace("kurly", 6012, "컬리", homepage = "kurly.com"),
        GreetingWorkspace("hyundai-autoever", 13782, "현대오토에버", homepage = "hyundai-autoever.com"),
        GreetingWorkspace("kakaomobility", 14346, "카카오모빌리티", homepage = "kakaomobility.com"),
        GreetingWorkspace("teamsparta", 2450, "팀스파르타", homepage = "teamsparta.co"),
        GreetingWorkspace("wadiz", 2855, "와디즈", homepage = "wadiz.kr"),
        GreetingWorkspace("lf", 7797, "LF", homepage = "lf.co.kr"),
        GreetingWorkspace("zigbang", 1724, "직방", homepage = "zigbang.com"),
        GreetingWorkspace("finda", 3037, "핀다", homepage = "finda.co.kr"),
        // 2026-06-28 2차 발굴(정찰 확정 ID + boot API로 원문 호스트 검증)
        GreetingWorkspace("jype", 12286, "JYP엔터테인먼트", homepage = "jype.com", originHost = "recruit.jype.com"),
        GreetingWorkspace("hugel", 3623, "휴젤", homepage = "hugel.co.kr"),
        GreetingWorkspace("gccompany", 3017, "여기어때", homepage = "goodchoice.kr"),
        GreetingWorkspace("sment", 11325, "SM엔터테인먼트", homepage = "smentertainment.com"),
        GreetingWorkspace("pulmuone", 16624, "풀무원", homepage = "pulmuone.co.kr"),
        GreetingWorkspace("oci", 2252, "OCI", homepage = "oci.co.kr"),
        GreetingWorkspace("phcgroup", 10553, "평화정공", homepage = "phc.co.kr"),
    )

    /**
     * Workday(CXS) 테넌트. 인증 불필요 공개 API. host·cxsTenant·site는 회사 채용사이트 URL로 직접 확인.
     * 2026-06-28 라이브 검증: 대웅제약(61건). 더 추가하려면 회사별 myworkdayjobs 호스트/사이트 확인.
     */
    val workdayTenants: List<WorkdayTenant> = listOf(
        WorkdayTenant("daewoong.impl-wd102", "daewoong", "External", "대웅제약", homepage = "daewoong.com"),
    )
}
