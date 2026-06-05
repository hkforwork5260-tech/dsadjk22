package com.jobalert.backend.service

import org.springframework.stereotype.Component

/**
 * 공고를 21개 직군으로 자동 분류 (규칙 기반 키워드 매칭).
 *
 * 수집 공고에는 직군 코드가 없어서 카테고리 필터가 무용지물이었다. 제목 + 부서(공공기관은 NCS 분류명)
 * + 키워드를 소문자로 합쳐 직군별 키워드와 매칭, 해당하는 코드를 모두 부여한다(복수 가능).
 *
 * 코드 키는 백엔드↔안드로이드 공유(API_CONTRACT 21개 표). AI 분류(Claude)는 v0.2 정확도 개선 시 검토.
 * 한 개도 안 걸리면 빈 리스트(미분류) — 필터에선 안 잡히지만 전체 목록엔 정상 노출.
 */
@Component
class JobCategoryClassifier {

    /** 제목·부서·키워드를 합쳐 매칭되는 직군 코드 목록 반환. 우선순위 높은(구체적) 직군부터. */
    fun classify(title: String?, department: String? = null, keywords: List<String> = emptyList()): List<String> {
        val haystack = buildString {
            append(title?.lowercase() ?: "")
            append(' ')
            append(department?.lowercase() ?: "")
            append(' ')
            append(keywords.joinToString(" ") { it.lowercase() })
        }
        if (haystack.isBlank()) return emptyList()

        return RULES.mapNotNull { (code, kws) ->
            code.takeIf { kws.any { kw -> haystack.contains(kw) } }
        }
    }

    companion object {
        // 직군 코드 → 키워드(소문자). 구체적/특이 직군을 앞에 둬서 가독성↑(매칭은 전부 검사).
        // 키워드는 부분 문자열 매칭이므로 짧고 흔한 단어("it" 등)는 오탐 주의해서 배제.
        private val RULES: List<Pair<String, List<String>>> = listOf(
            "medical" to listOf("간호", "간호사", "간호조무", "의사", "약사", "의료", "보건", "임상", "방사선",
                "물리치료", "작업치료", "임상병리", "응급구조", "위생사", "치과", "재활", "병동", "nurse", "의무"),
            "it_dev_data" to listOf("개발자", "개발 ", "엔지니어", "engineer", "developer", "백엔드", "프론트엔드",
                "backend", "frontend", "데이터", "data", "소프트웨어", "software", "프로그래", "서버", "devops",
                "인프라", "머신러닝", "machine learning", "ai ", "ml ", "안드로이드", "android", "ios", "웹개발",
                "정보보안", "정보통신", "시스템 운영", "데브옵스", "qa"),
            "research" to listOf("연구", "r&d", "연구원", "researcher", "연구개발", "박사", "포스닥", "실험", "연구직"),
            "design" to listOf("디자인", "디자이너", "designer", "ux", "ui", "bx", "그래픽", "편집디자인"),
            "construction" to listOf("건설", "건축", "토목", "시공", "감리", "현장소장", "전기공사", "플랜트설치", "건축설계"),
            "production" to listOf("생산", "제조", "공정", "설비", "품질관리", "qc", "정비", "기능직", "설치공",
                "조립", "용접", "노무원", "특별인부", "플랜트", "기계설치", "전공", "운전원", "오퍼레이터"),
            "finance_insurance" to listOf("금융", "보험", "은행", "증권", "투자", "펀드", "여신", "자산운용", "계리", "actuary"),
            "accounting_finance" to listOf("회계", "세무", "재무", "자금", "accounting", "finance", "경리", "결산", "예산"),
            "hr_hrd" to listOf("인사", "노무", "hrd", "채용담당", "인재", "교육담당", "인사관리", "people team"),
            "marketing_pr" to listOf("마케팅", "marketing", "홍보", "pr ", "광고", "브랜드", "brand", "퍼포먼스", "그로스"),
            "plan_strategy" to listOf("기획", "전략", "strategy", "planning", "프로덕트 매니저", "product manager",
                "사업개발", "경영기획", " po ", " pm "),
            "sales_trade" to listOf("영업", "판매", "무역", "sales", "세일즈", "영업관리", "account manager", "수출입"),
            "customer_tm" to listOf("고객상담", "상담원", "cs ", "customer", "콜센터", "텔레마케팅", "고객지원", "고객센터"),
            "purchase_logistics" to listOf("구매", "자재", "물류", "logistics", "scm", "조달", "재고", "공급망", "창고관리"),
            "md_planning" to listOf("상품기획", "머천다이저", " md ", "바이어", "buyer", "상품개발"),
            "driving_delivery" to listOf("운전", "운송", "배송", "기사", "배달", "택배", "driver", "delivery", "운수"),
            "education" to listOf("교육", "강사", "교사", "교수", "보육", "교직", "직업훈련", "훈련교사", "teacher", "학습지"),
            "media_culture_sport" to listOf("미디어", "방송", "콘텐츠", "영상", "기자", " pd ", "문화", "예술", "스포츠",
                "체육", "공연", "큐레이터", "아나운서"),
            "public_welfare" to listOf("사회복지", "복지사", "돌봄", "요양", "보훈", "자원봉사", "복지", "공공기관"),
            "admin_legal" to listOf("총무", "법무", "법률", "변호사", "비서", "사무보조", "사무직", "서무", "행정", "일반사무"),
            "service" to listOf("미화", "청소", "경비", "시설관리", "조리", "주방", "매장", "접객", "안내", "환경미화"),
        )
    }
}
