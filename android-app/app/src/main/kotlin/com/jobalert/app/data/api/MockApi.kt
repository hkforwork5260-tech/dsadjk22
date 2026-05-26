package com.jobalert.app.data.api

/**
 * 백엔드 붙기 전 임시 mock. API_CONTRACT.md의 응답 형식을 그대로 반환.
 * 추후 Retrofit 인터페이스로 대체될 때 호출부 시그니처는 그대로 두고 구현체만 바꿈.
 *
 * 시간 표기는 UTC ISO8601. (오늘은 2026-05-26 KST 기준 가정)
 */
object MockApi {

    // ─── 공통 회사 풀 ───
    private val samsung = CompanyDto(
        id = 1, name = "삼성전자", logo = "삼성",
        logoUrl = "https://logo.clearbit.com/samsung.com",
        industry = "전기·전자", group = "삼성", size = "large_corp",
        activeJobCount = 12, isFavorited = true,
    )
    private val naver = CompanyDto(
        id = 2, name = "네이버", logo = "N",
        industry = "IT·플랫폼", group = "네이버", size = "large_corp",
        activeJobCount = 4,
    )
    private val lges = CompanyDto(
        id = 3, name = "LG에너지솔루션", logo = "LG",
        industry = "배터리·화학", group = "LG", size = "large_corp",
        activeJobCount = 6,
    )
    private val kakao = CompanyDto(
        id = 4, name = "카카오", logo = "카",
        industry = "IT·플랫폼", group = "카카오", size = "large_corp",
        activeJobCount = 8,
    )
    private val posco = CompanyDto(
        id = 5, name = "포스코", logo = "포",
        industry = "철강", group = "포스코", size = "large_corp",
        activeJobCount = 3,
    )
    private val hmobis = CompanyDto(
        id = 6, name = "현대모비스", logo = "HM",
        industry = "자동차부품", group = "현대차", size = "large_corp",
        activeJobCount = 5,
    )
    private val hyundai = CompanyDto(
        id = 7, name = "현대자동차", logo = "현",
        industry = "자동차", group = "현대차", size = "large_corp",
        activeJobCount = 2,
    )
    private val cj = CompanyDto(
        id = 8, name = "CJ제일제당", logo = "CJ",
        industry = "식품", group = "CJ", size = "large_corp",
        activeJobCount = 1,
    )
    private val skhynix = CompanyDto(
        id = 9, name = "SK하이닉스", logo = "SK",
        industry = "반도체", group = "SK", size = "large_corp",
        activeJobCount = 7,
    )

    // 온보딩 ③ 회사 스와이프용 (Tinder 카드)
    private val doosan = CompanyDto(
        id = 10, name = "두산에너빌리티", logo = "두산",
        industry = "에너지·중공업", size = "large_corp",
        activeJobCount = 5,
    )
    private val kt = CompanyDto(
        id = 11, name = "KT", logo = "KT",
        industry = "통신·IT", size = "large_corp", activeJobCount = 3,
    )
    private val amore = CompanyDto(
        id = 12, name = "아모레퍼시픽", logo = "아모",
        industry = "화장품·뷰티", size = "large_corp", activeJobCount = 2,
    )
    private val hanwhaSys = CompanyDto(
        id = 13, name = "한화시스템", logo = "한화",
        industry = "방산·IT", size = "large_corp", activeJobCount = 4,
    )
    private val coupang = CompanyDto(
        id = 14, name = "쿠팡", logo = "쿠팡",
        industry = "커머스·물류", size = "large_corp", activeJobCount = 7,
    )

    // ─── /api/v1/onboarding/categories ───
    fun categories(): CategoriesResponse = CategoriesResponse(
        categories = listOf(
            JobCategoryDto("plan_strategy", "기획·전략"),
            JobCategoryDto("marketing_pr", "마케팅·홍보·조사"),
            JobCategoryDto("accounting_finance", "회계·세무·재무"),
            JobCategoryDto("hr_hrd", "인사·노무·HRD"),
            JobCategoryDto("general_admin", "총무·법무·사무"),
            JobCategoryDto("it_dev_data", "IT개발·데이터"),
            JobCategoryDto("design", "디자인"),
            JobCategoryDto("sales_trade", "영업·판매·무역"),
            JobCategoryDto("cs_tm", "고객상담·TM"),
            JobCategoryDto("scm_logistics", "구매·자재·물류"),
            JobCategoryDto("md_planning", "상품기획·MD"),
            JobCategoryDto("driving", "운전·운송·배송"),
            JobCategoryDto("service", "서비스"),
            JobCategoryDto("production", "생산"),
            JobCategoryDto("construction", "건설·건축"),
            JobCategoryDto("medical", "의료"),
            JobCategoryDto("rnd", "연구·R&D"),
            JobCategoryDto("education", "교육"),
            JobCategoryDto("media_sports", "미디어·문화·스포츠"),
            JobCategoryDto("finance_insurance", "금융·보험"),
            JobCategoryDto("public_welfare", "공공·복지"),
        )
    )

    // ─── /api/v1/onboarding/popular-companies ───
    fun popularCompanies(): PopularCompaniesResponse = PopularCompaniesResponse(
        companies = listOf(doosan, kt, amore, hanwhaSys, coupang),
    )

    // ─── /api/v1/jobs/today (NEW/UPDATE/CLOSING 모두 묶어서) ───
    fun jobsToday(): JobsTodayResponse = JobsTodayResponse(
        date = "2026-05-26",
        counts = mapOf("new" to 6, "update" to 1, "closing" to 1),
        jobs = listOf(
            JobDto(
                id = "samsung-2026-h1", company = samsung,
                title = "2026 상반기 신입공채", kind = "NEW",
                dday = "D-24", deadline = "2026-06-15T14:59:59Z",
                location = "수원", experience = "신입", education = "학사+",
                tags = listOf("반도체", "DS", "신입공채"),
            ),
            JobDto(
                id = "naver-backend", company = naver,
                title = "신입 백엔드 개발자", kind = "NEW",
                dday = "D-19", deadline = "2026-06-10T14:59:59Z",
                location = "판교", experience = "신입", education = "학사+",
                tags = listOf("Java", "Kotlin", "Spring"),
            ),
            JobDto(
                id = "lges-rnd", company = lges,
                title = "연구개발(R&D) 신입", kind = "NEW",
                dday = "D-16", deadline = "2026-06-07T14:59:59Z",
                location = "대전", experience = "신입", education = "학사+",
                tags = listOf("배터리", "R&D"),
            ),
            JobDto(
                id = "kakao-android", company = kakao,
                title = "신입 안드로이드 개발자", kind = "NEW",
                dday = "D-14", deadline = "2026-06-05T14:59:59Z",
                location = "판교",
                tags = listOf("Android", "Kotlin", "Compose"),
            ),
            JobDto(
                id = "posco-2026-h1", company = posco,
                title = "2026 상반기 신입공채", kind = "NEW",
                dday = "D-12", deadline = "2026-06-03T14:59:59Z",
                location = "포항",
            ),
            JobDto(
                id = "hmobis-rnd", company = hmobis,
                title = "기계 R&D 신입", kind = "NEW",
                dday = "D-10", deadline = "2026-06-01T14:59:59Z",
                location = "용인",
            ),
            JobDto(
                id = "kakao-fe-update", company = kakao,
                title = "경력 프론트엔드 개발자", kind = "UPDATE",
                dday = "D-6", deadline = "2026-05-28T14:59:59Z",
                location = "판교",
            ),
            JobDto(
                id = "hyundai-closing", company = hyundai,
                title = "신입사원 일반공채", kind = "CLOSING",
                dday = "D-1", deadline = "2026-05-27T14:59:59Z",
                location = "서울",
            ),
        ),
    )

    /** 메인 빈 상태 — NEW 0건이지만 CLOSING 임박 공고는 있음. */
    fun jobsTodayEmpty(): JobsTodayResponse = JobsTodayResponse(
        date = "2026-05-26",
        counts = mapOf("new" to 0, "update" to 1, "closing" to 2),
        jobs = listOf(
            JobDto(
                id = "hyundai-closing", company = hyundai,
                title = "신입사원 일반공채", kind = "CLOSING",
                dday = "D-1", deadline = "2026-05-27T14:59:59Z",
                location = "서울",
            ),
            JobDto(
                id = "cj-closing", company = cj,
                title = "CJ 신입공채", kind = "CLOSING",
                dday = "D-0", deadline = "2026-05-26T14:59:59Z",
                location = "서울",
            ),
            JobDto(
                id = "skhynix-update", company = skhynix,
                title = "2026 신입사원 채용", kind = "UPDATE",
                dday = "D-11", deadline = "2026-06-02T14:59:59Z",
                location = "이천",
            ),
        ),
    )

    // ─── /api/v1/companies/{id} ───
    /**
     * 공고 있음(samsung=1) / 공고 없음(doosan_bobcat=99) 두 케이스만 demo.
     */
    fun companyDetail(id: Int): CompanyDetailResponse {
        return when (id) {
            99 -> CompanyDetailResponse(
                company = CompanyDto(
                    id = 99, name = "두산밥캣", logo = "두산",
                    industry = "건설기계·산업기계", group = "두산", size = "large_corp",
                    activeJobCount = 0, isFavorited = true,
                    description = "소형 건설장비 글로벌 1위. 북미 시장 매출 비중 70%.",
                ),
                region = "인천",
                about = "소형 건설장비 글로벌 1위. 북미 시장 매출 비중 70%.",
                stats = CompanyStats(thisYearCount = 8, avgCloseLabel = "2주", passRateLabel = "6%"),
                postings = emptyList(),
                history = listOf(
                    JobHistoryDto("2026 상반기 신입공채", "5/1 ~ 5/14 마감"),
                    JobHistoryDto("재무 경력직", "4/15 ~ 4/30 마감"),
                    JobHistoryDto("글로벌 영업 (5년+)", "3/10 ~ 4/3 마감"),
                ),
            )
            else -> CompanyDetailResponse(
                company = samsung.copy(
                    description = "반도체(DS), 디스플레이, 모바일·생활가전(DX) 등 사업부문 운영. 글로벌 1위 메모리 반도체 기업.",
                ),
                region = "서울/수원/화성",
                about = "반도체(DS), 디스플레이, 모바일·생활가전(DX) 등 사업부문 운영. 글로벌 1위 메모리 반도체 기업.",
                stats = CompanyStats(thisYearCount = 24, avgCloseLabel = "3주", passRateLabel = "4%"),
                postings = listOf(
                    JobDto(
                        id = "samsung-2026-h1", company = samsung,
                        title = "2026 상반기 신입공채", kind = "NEW",
                        dday = "D-24", deadline = "2026-06-15T14:59:59Z",
                        location = "수원",
                    ),
                    JobDto(
                        id = "samsung-ds-memory", company = samsung,
                        title = "DS 부문 메모리 R&D", kind = "NEW",
                        dday = "D-20", deadline = "2026-06-11T14:59:59Z",
                        location = "화성",
                    ),
                    JobDto(
                        id = "samsung-dx-sw", company = samsung,
                        title = "DX 부문 SW 경력직", kind = "UPDATE",
                        dday = "D-35", deadline = "2026-06-26T14:59:59Z",
                        location = "수원",
                    ),
                    JobDto(
                        id = "samsung-global-mkt", company = samsung,
                        title = "글로벌 마케팅 (3년+)", kind = "NEW",
                        dday = "D-18", deadline = "2026-06-09T14:59:59Z",
                        location = "서울",
                    ),
                ),
                history = emptyList(),
            )
        }
    }

    // ─── /api/v1/users/me/favorites ───
    fun favorites(): FavoritesResponse = FavoritesResponse(
        companies = listOf(
            FavoriteCompanyDto(samsung, newCount = 3),
            FavoriteCompanyDto(naver, newCount = 2),
            FavoriteCompanyDto(kakao, newCount = 1, hasAlarm = false),
            FavoriteCompanyDto(lges, newCount = 1),
            FavoriteCompanyDto(skhynix.copy(id = 99, name = "두산밥캣", logo = "두산"), newCount = 0, hasAlarm = false),
            FavoriteCompanyDto(hyundai, newCount = 2),
            FavoriteCompanyDto(cj, newCount = 0, hasAlarm = false),
            FavoriteCompanyDto(posco, newCount = 1),
        ),
    )

    // ─── /api/v1/notifications/history ───
    fun notifications(): NotificationsResponse = NotificationsResponse(
        notifications = listOf(
            NotificationDto(
                id = "ntf-001",
                sentAt = "2026-05-26T00:00:00Z",
                kind = "morning_digest",
                title = "오늘 새 공고 6건 ☀️",
                body = "삼성전자, 네이버, 카카오 외 3건이 올라왔어요",
                jobIds = listOf("samsung-2026-h1", "naver-backend", "kakao-android"),
                read = false,
            ),
            NotificationDto(
                id = "ntf-002",
                sentAt = "2026-05-25T12:00:00Z",
                kind = "evening_digest",
                title = "마감 임박 2건 🔥",
                body = "현대자동차 신입공채 (D-1), CJ제일제당 (D-0)",
                jobIds = listOf("hyundai-closing", "cj-closing"),
                read = false,
            ),
            NotificationDto(
                id = "ntf-003",
                sentAt = "2026-05-25T00:00:00Z",
                kind = "morning_digest",
                title = "오늘 새 공고 4건 ☀️",
                body = "LG에너지솔루션, 포스코 외 2건",
                jobIds = listOf("lges-rnd", "posco-2026-h1"),
                read = true,
            ),
            NotificationDto(
                id = "ntf-004",
                sentAt = "2026-05-24T00:00:00Z",
                kind = "morning_digest",
                title = "오늘 새 공고 11건 ☀️",
                body = "관심기업에서 새 공고가 다수 올라왔어요",
                jobIds = emptyList(),
                read = true,
            ),
            NotificationDto(
                id = "ntf-005",
                sentAt = "2026-05-23T12:00:00Z",
                kind = "evening_digest",
                title = "마감 임박 1건 🔥",
                body = "두산에너빌리티 (D-3)",
                jobIds = emptyList(),
                read = true,
            ),
        ),
    )

    // ─── /api/v1/jobs/upcoming (캘린더) ───
    fun upcoming(): UpcomingResponse = UpcomingResponse(
        days = 30,
        byDate = mapOf(
            "2026-05-27" to listOf(
                JobDto(id = "hyundai-closing", company = hyundai, title = "신입사원 일반공채",
                    kind = "CLOSING", dday = "D-1", deadline = "2026-05-27T14:59:59Z", location = "서울"),
            ),
            "2026-05-28" to listOf(
                JobDto(id = "kakao-fe-update", company = kakao, title = "경력 프론트엔드 개발자",
                    kind = "UPDATE", dday = "D-2", deadline = "2026-05-28T14:59:59Z", location = "판교"),
            ),
            "2026-06-01" to listOf(
                JobDto(id = "hmobis-rnd", company = hmobis, title = "기계 R&D 신입",
                    kind = "NEW", dday = "D-6", deadline = "2026-06-01T14:59:59Z", location = "용인"),
            ),
            "2026-06-03" to listOf(
                JobDto(id = "posco-2026-h1", company = posco, title = "2026 상반기 신입공채",
                    kind = "NEW", dday = "D-8", deadline = "2026-06-03T14:59:59Z", location = "포항"),
            ),
            "2026-06-05" to listOf(
                JobDto(id = "kakao-android", company = kakao, title = "신입 안드로이드 개발자",
                    kind = "NEW", dday = "D-10", deadline = "2026-06-05T14:59:59Z", location = "판교"),
            ),
            "2026-06-07" to listOf(
                JobDto(id = "lges-rnd", company = lges, title = "연구개발(R&D) 신입",
                    kind = "NEW", dday = "D-12", deadline = "2026-06-07T14:59:59Z", location = "대전"),
            ),
            "2026-06-10" to listOf(
                JobDto(id = "naver-backend", company = naver, title = "신입 백엔드 개발자",
                    kind = "NEW", dday = "D-15", deadline = "2026-06-10T14:59:59Z", location = "판교"),
            ),
            "2026-06-15" to listOf(
                JobDto(id = "samsung-2026-h1", company = samsung, title = "2026 상반기 신입공채",
                    kind = "NEW", dday = "D-20", deadline = "2026-06-15T14:59:59Z", location = "수원"),
                JobDto(id = "samsung-ds-memory", company = samsung, title = "DS 부문 메모리 R&D",
                    kind = "NEW", dday = "D-20", deadline = "2026-06-15T14:59:59Z", location = "화성"),
            ),
        ),
    )

    // ─── /api/v1/jobs/search ───
    /**
     * 키워드 "삼성" 검색 결과. 백엔드 붙으면 q 파라미터로 분기.
     */
    fun jobsSearch(query: String): JobsSearchResponse {
        // 데모용 — 항상 "삼성" 결과 반환
        val sams1 = samsung
        val sams2 = CompanyDto(id = 21, name = "삼성SDS", logo = "삼성", industry = "IT·SI", group = "삼성", size = "large_corp", activeJobCount = 1)
        val sams3 = CompanyDto(id = 22, name = "삼성바이오로직스", logo = "삼성", industry = "바이오", group = "삼성", size = "large_corp", activeJobCount = 0)
        val sams4 = CompanyDto(id = 23, name = "삼성생명", logo = "삼성", industry = "보험", group = "삼성", size = "large_corp", activeJobCount = 0)
        return JobsSearchResponse(
            query = query,
            totalEstimate = 12,
            companies = listOf(sams1, sams2, sams3, sams4),
            jobs = listOf(
                JobDto(
                    id = "samsung-2026-h1", company = sams1,
                    title = "2026 상반기 신입공채", kind = "NEW",
                    dday = "D-24", deadline = "2026-06-15T14:59:59Z",
                    location = "수원", tags = listOf("반도체", "신입공채"),
                ),
                JobDto(
                    id = "samsungsds-cloud", company = sams2,
                    title = "클라우드 신입사원", kind = "NEW",
                    dday = "D-20", deadline = "2026-06-11T14:59:59Z",
                    location = "서울",
                ),
                JobDto(
                    id = "samsung-ds-exp", company = sams1,
                    title = "DS부문 경력직", kind = "UPDATE",
                    dday = "D-30", deadline = "2026-06-21T14:59:59Z",
                    location = "수원",
                ),
            ),
        )
    }
}
