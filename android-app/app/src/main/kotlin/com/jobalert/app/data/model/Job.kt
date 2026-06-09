package com.jobalert.app.data.model

import com.jobalert.app.ui.theme.JobKind
import kotlinx.serialization.Serializable

/**
 * 공고 모델. 백엔드 스펙 나오면 fromDto/toEntity 추가.
 */
@Serializable
data class Job(
    val id: String,
    val company: String,
    val companyId: Int? = null,        // 회사 상세 이동용 (상세 화면에서 사용)
    val logo: String,                  // 로고용 짧은 텍스트 (e.g. "삼성", "N")
    val role: String,
    val kind: JobKind,
    val dday: String,                  // "D-19"
    val dateText: String,              // "~6/10"
    val location: String = "",
    val experience: String = "",       // "신입", "3년+"
    val education: String = "",        // "학사+"
    val salary: String = "",           // 급여 (서울시 소스 — "최소연봉 3200만원" 등)
    val tags: List<String> = emptyList(),
    val categories: List<String> = emptyList(),  // 직군 한글 라벨 (카드·상세 배지)
    val companySize: String = "",                // 회사규모 코드 (large_corp/public/...)
    val description: String = "",                 // 공고 본문(평문) — 상세 화면
    val originalUrl: String = "",
    val isSaved: Boolean = false,       // 저장(북마크) 여부 — 공고 상세에서 사용
    val isFavoriteCompany: Boolean = false,  // 이 공고 회사가 관심기업인지 — 상세 하트 초기상태
)

private val enRegion = mapOf(
    "seoul" to "서울", "gyeonggi" to "경기", "incheon" to "인천",
    "busan" to "부산", "daegu" to "대구", "daejeon" to "대전",
    "gwangju" to "광주", "ulsan" to "울산", "sejong" to "세종",
    "gangwon" to "강원", "jeju" to "제주", "remote" to "원격",
)

/**
 * 카드 로고 자리에 표시할 짧은 근무지역(회사 위치 아님, 공고의 근무지).
 * 예: "Seoul, South Korea"→"서울", "서울특별시 강남구"→"서울", "경기"→"경기".
 * location 없으면 logo(회사 이니셜)로 폴백.
 */
val Job.regionShort: String
    get() {
        if (location.isBlank()) return logo
        val first = location.split(",", "·").firstOrNull { it.isNotBlank() }
            ?.trim()?.split(" ")?.firstOrNull { it.isNotBlank() } ?: return logo
        enRegion[first.lowercase()]?.let { return it }
        val stripped = first
            .removeSuffix("특별자치시").removeSuffix("특별자치도")
            .removeSuffix("특별시").removeSuffix("광역시")
            .removeSuffix("도").removeSuffix("시")
        return stripped.ifBlank { logo }.take(4)
    }

/**
 * 22개 직군. README 21개 + "기타" 추가.
 */
val JobCategories: List<String> = listOf(
    "기획·전략", "마케팅·홍보·조사", "회계·세무·재무",
    "인사·노무·HRD", "총무·법무·사무", "IT개발·데이터",
    "디자인", "영업·판매·무역", "고객상담·TM",
    "구매·자재·물류", "상품기획·MD", "운전·운송·배송",
    "서비스", "생산", "건설·건축",
    "의료", "연구·R&D", "교육",
    "미디어·문화·스포츠", "금융·보험", "공공·복지",
    "기타",
)

/**
 * [JobCategories]와 같은 순서의 백엔드 직군 코드. 필터 화면의 인덱스 선택을 코드로 변환할 때 사용.
 * 마지막 "기타"="etc" — 백엔드 분류기가 미매칭 공고에 부여하는 catch-all 코드(라벨·필터 동작).
 */
val JobCategoryCodes: List<String> = listOf(
    "plan_strategy", "marketing_pr", "accounting_finance",
    "hr_hrd", "admin_legal", "it_dev_data",
    "design", "sales_trade", "customer_tm",
    "purchase_logistics", "md_planning", "driving_delivery",
    "service", "production", "construction",
    "medical", "research", "education",
    "media_culture_sport", "finance_insurance", "public_welfare",
    "etc",
)
