package com.jobalert.backend.service

import org.springframework.stereotype.Component

/**
 * 공고를 직군으로 자동 분류 (규칙 기반 키워드 매칭).
 *
 * 신호는 제목 + 부서(공공기관 NCS·서울 직종명) + 키워드(소스별 직종 신호)만 사용한다.
 * 본문(description)·혜택 태그(4대보험·퇴직금 등)는 보일러플레이트가 많아 오분류를 유발하므로 제외.
 * 어디에도 안 걸리면 "etc"(기타) — 미분류로 사라지지 않게 catch-all.
 *
 * 영문 키워드를 보강(Greenhouse 글로벌 공고 대응). 코드 키는 백엔드↔안드 공유(21 + etc).
 */
@Component
class JobCategoryClassifier {

    /** 제목·부서·키워드를 합쳐 매칭되는 직군 코드 목록. 하나도 없으면 ["etc"]. */
    fun classify(
        title: String?,
        department: String? = null,
        keywords: List<String> = emptyList(),
    ): List<String> {
        val haystack = buildString {
            append(title?.lowercase() ?: "")
            append(' ')
            append(department?.lowercase() ?: "")
            append(' ')
            append(keywords.joinToString(" ") { it.lowercase() })
        }
        if (haystack.isBlank()) return listOf("etc")
        val hits = RULES.mapNotNull { (code, kws) ->
            code.takeIf { kws.any { kw -> haystack.contains(kw) } }
        }
        return hits.ifEmpty { listOf("etc") }
    }

    companion object {
        // 직군 코드 → 키워드(소문자). 부분 문자열 매칭이라 흔한 단어("보험"=4대보험, "투자"=투자유치,
        // "인재"=인재상, "assistant" 등)는 오탐 방지 위해 구체 구문으로만.
        private val RULES: List<Pair<String, List<String>>> = listOf(
            "medical" to listOf("간호", "간호사", "간호조무", "의사", "약사", "의료", "보건", "임상", "방사선",
                "물리치료", "작업치료", "임상병리", "응급구조", "위생사", "치과", "재활", "병동", "의무",
                "nurse", "clinical", "pharmacist", "physician", "medical", "healthcare", "therapist"),
            "it_dev_data" to listOf("개발자", "엔지니어", "백엔드", "프론트엔드", "데이터분석", "데이터 분석",
                "데이터엔지니어", "소프트웨어", "프로그래", "서버개발", "인프라", "머신러닝", "딥러닝", "안드로이드",
                "웹개발", "정보보안", "보안관제", "정보통신", "데브옵스", "데이터 사이언",
                "engineer", "developer", "backend", "frontend", "full stack", "fullstack", "full-stack",
                "software", "data scientist", "data engineer", "data analyst", "machine learning", " ml ",
                "devops", " sre ", "infrastructure", "platform engineer", "security engineer",
                " ios ", "android", "mobile engineer", "web developer", "qa engineer", "analytics engineer"),
            "research" to listOf("연구", "r&d", "연구원", "연구개발", "포스닥", "실험", "연구직",
                "research scientist", "researcher", "research engineer"),
            "design" to listOf("디자인", "디자이너", " ux", " ui", " bx", "그래픽", "편집디자인", "모션그래",
                "designer", "graphic", "product design", "brand design", "motion design", "illustrat"),
            "construction" to listOf("건설", "건축", "토목", "시공", "감리", "현장소장", "전기공사", "플랜트설치",
                "건축설계", "construction", "civil engineer", "architect"),
            "production" to listOf("생산", "제조", "공정", "설비", "품질관리", " qc", "정비", "기능직", "설치공",
                "조립", "용접", "노무원", "특별인부", "플랜트", "기계설치", "운전원", "오퍼레이터",
                "manufacturing", "production", "operator", "technician", "assembly", "machinist"),
            "finance_insurance" to listOf("은행", "증권", "여신", "자산운용", "보험사", "보험설계", "손해보험",
                "생명보험", "펀드매니저", "계리", "actuary", "investment analyst", "banking", "underwrit"),
            "accounting_finance" to listOf("회계", "세무", "재무", "자금", "경리", "결산", "예산",
                "accounting", "accountant", "financial analyst", "controller", "fp&a", "treasury", " tax ", "auditor"),
            "hr_hrd" to listOf("인사", "노무", "hrd", "채용담당", "인사관리", "교육담당",
                "recruiter", "recruiting", "talent acquisition", "people operations", "people team",
                "human resources", "hrbp"),
            "marketing_pr" to listOf("마케팅", "홍보", " pr ", "광고", "브랜드마케", "퍼포먼스마케", "그로스",
                "marketing", "marketer", "growth", "brand manager", "communications", "social media", "advertis"),
            "plan_strategy" to listOf("기획", "전략", "사업개발", "경영기획", "프로덕트 매니저",
                "strategy", "product manager", "program manager", "project manager", "business development",
                "operations manager", "chief of staff"),
            "sales_trade" to listOf("영업", "판매", "무역", "세일즈", "영업관리", "수출입", "파트너십",
                "sales", "account executive", "account manager", "partnership"),
            "customer_tm" to listOf("고객상담", "상담원", "콜센터", "텔레마케팅", "고객지원", "고객센터",
                "customer support", "customer success", "support specialist"),
            "purchase_logistics" to listOf("구매", "자재", "물류", "scm", "조달", "재고관리", "공급망", "창고관리",
                "logistics", "supply chain", "procurement", "warehouse", "fulfillment"),
            "md_planning" to listOf("상품기획", "머천다이저", " md ", "바이어", "상품개발", "buyer", "merchandis"),
            "driving_delivery" to listOf("운전", "운송", "배송", "기사", "배달", "택배", "운수",
                "driver", "delivery", "courier"),
            "education" to listOf("교육", "강사", "교사", "교수", "보육", "교직", "직업훈련", "훈련교사", "학습지",
                "teacher", "instructor", "trainer", "tutor"),
            "media_culture_sport" to listOf("미디어", "방송", "콘텐츠", "영상편집", "기자", " pd ", "문화예술",
                "스포츠", "체육", "공연", "큐레이터", "아나운서", "editor", "videographer", "journalist"),
            "public_welfare" to listOf("사회복지", "복지사", "돌봄", "요양", "보훈", "자원봉사", "공공기관"),
            "admin_legal" to listOf("총무", "법무", "법률", "변호사", "비서", "사무보조", "사무직", "서무", "행정",
                "일반사무", "legal", "compliance", "paralegal", "office manager", "executive assistant",
                "administrative assistant"),
            "service" to listOf("미화", "청소", "경비", "시설관리", "조리", "주방", "매장관리", "접객", "안내데스크",
                "환경미화", "barista", "facility", "housekeeping", "security guard"),
        )
    }
}
