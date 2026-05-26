package com.jobalert.app.data.model

import com.jobalert.app.ui.theme.JobKind

/**
 * 공고 모델. 백엔드 스펙 나오면 fromDto/toEntity 추가.
 */
data class Job(
    val id: String,
    val company: String,
    val logo: String,                  // 로고용 짧은 텍스트 (e.g. "삼성", "N")
    val role: String,
    val kind: JobKind,
    val dday: String,                  // "D-19"
    val dateText: String,              // "~6/10"
    val location: String = "",
    val experience: String = "",       // "신입", "3년+"
    val education: String = "",        // "학사+"
    val summary: String = "",          // 꽁이의 한줄 요약 (AI)
    val tags: List<String> = emptyList(),
    val originalUrl: String = "",
)

/**
 * 21개 직군. README 표 그대로.
 */
val JobCategories: List<String> = listOf(
    "기획·전략", "마케팅·홍보·조사", "회계·세무·재무",
    "인사·노무·HRD", "총무·법무·사무", "IT개발·데이터",
    "디자인", "영업·판매·무역", "고객상담·TM",
    "구매·자재·물류", "상품기획·MD", "운전·운송·배송",
    "서비스", "생산", "건설·건축",
    "의료", "연구·R&D", "교육",
    "미디어·문화·스포츠", "금융·보험", "공공·복지"
)
