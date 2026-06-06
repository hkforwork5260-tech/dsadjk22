package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDto
import com.jobalert.backend.dto.CompanyEmbedDto
import com.jobalert.backend.dto.JobCategoryDto
import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobDto
import com.jobalert.backend.dto.NotificationDto
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Phase 1 mock 데이터. android-app SampleJobs.kt와 정렬.
 * Phase 3에서 실제 DB 조회로 교체.
 */
@Component
class MockDataProvider {

    private val now: OffsetDateTime = OffsetDateTime.of(2026, 5, 26, 9, 0, 0, 0, ZoneOffset.UTC)

    val categories: List<JobCategoryDto> = listOf(
        JobCategoryDto("plan_strategy", "기획·전략"),
        JobCategoryDto("marketing_pr", "마케팅·홍보·조사"),
        JobCategoryDto("accounting_finance", "회계·세무·재무"),
        JobCategoryDto("hr_hrd", "인사·노무·HRD"),
        JobCategoryDto("admin_legal", "총무·법무·사무"),
        JobCategoryDto("it_dev_data", "IT개발·데이터"),
        JobCategoryDto("design", "디자인"),
        JobCategoryDto("sales_trade", "영업·판매·무역"),
        JobCategoryDto("customer_tm", "고객상담·TM"),
        JobCategoryDto("purchase_logistics", "구매·자재·물류"),
        JobCategoryDto("md_planning", "상품기획·MD"),
        JobCategoryDto("driving_delivery", "운전·운송·배송"),
        JobCategoryDto("service", "서비스"),
        JobCategoryDto("production", "생산"),
        JobCategoryDto("construction", "건설·건축"),
        JobCategoryDto("medical", "의료"),
        JobCategoryDto("research", "연구·R&D"),
        JobCategoryDto("education", "교육"),
        JobCategoryDto("media_culture_sport", "미디어·문화·스포츠"),
        JobCategoryDto("finance_insurance", "금융·보험"),
        JobCategoryDto("public_welfare", "공공·복지"),
    )

    val companies: List<CompanyDto> = listOf(
        CompanyDto(1, "삼성전자", "samsungelectronics", "https://logo.clearbit.com/samsung.com", "전기·전자", "삼성", "large_corp", "https://www.samsung.com/sec/", "https://www.samsungcareers.com/", 12),
        CompanyDto(2, "네이버", "naver", "https://logo.clearbit.com/naver.com", "인터넷·IT", "네이버", "large_corp", "https://www.naver.com/", "https://recruit.navercorp.com/", 7),
        CompanyDto(3, "카카오", "kakao", "https://logo.clearbit.com/kakao.com", "인터넷·IT", "카카오", "large_corp", "https://www.kakaocorp.com/", "https://careers.kakao.com/", 9),
        CompanyDto(4, "LG에너지솔루션", "lgenergysolution", "https://logo.clearbit.com/lgensol.com", "전기·전자", "LG", "large_corp", "https://www.lgensol.com/", "https://career.lgensol.com/", 5),
        CompanyDto(5, "포스코", "posco", "https://logo.clearbit.com/posco.com", "철강·금속", "포스코", "large_corp", "https://www.posco.com/", "https://careers.posco.com/", 3),
        CompanyDto(6, "현대모비스", "hyundaimobis", "https://logo.clearbit.com/mobis.co.kr", "자동차·부품", "현대차", "large_corp", "https://www.mobis.co.kr/", "https://career.mobis.com/", 4),
        CompanyDto(7, "현대자동차", "hyundaimotor", "https://logo.clearbit.com/hyundai.com", "자동차·부품", "현대차", "large_corp", "https://www.hyundai.com/", "https://careers.hyundai.com/", 8),
        CompanyDto(8, "SK하이닉스", "skhynix", "https://logo.clearbit.com/skhynix.com", "전기·전자", "SK", "large_corp", "https://www.skhynix.com/", "https://recruit.skhynix.com/", 6),
        CompanyDto(9, "토스", "toss", "https://logo.clearbit.com/toss.im", "핀테크", null, "startup_unicorn", "https://toss.im/", "https://toss.im/career", 11),
        CompanyDto(10, "쿠팡", "coupang", "https://logo.clearbit.com/coupang.com", "이커머스", null, "large_corp", "https://www.coupang.com/", "https://www.coupang.jobs/", 14),
    )

    private fun companyEmbed(id: Long): CompanyEmbedDto {
        val c = companies.first { it.id == id }
        // logo는 화면용 짧은 텍스트 (회사명 첫 2글자)
        val shortLogo = when (c.id) {
            1L -> "삼성"
            2L -> "N"
            3L -> "카카오"
            4L -> "LG"
            5L -> "포스"
            6L -> "HM"
            7L -> "현"
            else -> c.name.take(2)
        }
        return CompanyEmbedDto(c.id, c.name, shortLogo, c.logoUrl, c.industry)
    }

    val jobs: List<JobDto> = listOf(
        jobOf("samsung-2026-h1", 1L, "2026 상반기 신입공채", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 15),
            location = "수원", experience = "신입", education = "학사+",
            tags = listOf("반도체", "DS", "신입공채")),
        jobOf("naver-backend", 2L, "신입 백엔드 개발자", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 10),
            location = "판교", experience = "신입", education = "학사+",
            tags = listOf("Java", "Kotlin", "Spring")),
        jobOf("lges-rnd", 4L, "연구개발(R&D) 신입", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 7),
            location = "대전", experience = "신입", education = "학사+",
            tags = listOf("배터리", "R&D")),
        jobOf("kakao-android", 3L, "신입 안드로이드 개발자", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 5),
            location = "판교",
            tags = listOf("Android", "Kotlin", "Compose")),
        jobOf("posco-2026-h1", 5L, "2026 상반기 신입공채", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 3),
            location = "포항"),
        jobOf("hmobis-rnd", 6L, "기계 R&D 신입", "NEW",
            deadlineDate = LocalDate.of(2026, 6, 1),
            location = "용인"),
        jobOf("kakao-fe-update", 3L, "경력 프론트엔드 개발자", "UPDATE",
            deadlineDate = LocalDate.of(2026, 5, 28),
            location = "판교"),
        jobOf("hyundai-closing", 7L, "신입사원 일반공채", "CLOSING",
            deadlineDate = LocalDate.of(2026, 5, 27),
            location = "서울"),
    )

    val jobDetails: Map<String, JobDetailDto> = jobs.associate { j ->
        j.id to JobDetailDto(
            id = j.id,
            company = j.company,
            title = j.title,
            kind = j.kind,
            dday = j.dday,
            deadline = j.deadline,
            postingDate = j.deadline?.minus(30, ChronoUnit.DAYS),
            location = j.location,
            experience = j.experience,
            education = j.education,
            salary = "회사내규",
            jobCategories = listOf("IT개발·데이터"),
            tags = j.tags,
            description = "${j.company.name}에서 ${j.title}을 모집합니다. 상세 내용은 원문을 확인하세요.",
            preferred = listOf("관련 직무 경험", "팀워크 우수자"),
            process = listOf("서류전형", "1차 면접", "최종 면접"),
            originalUrl = "https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=${j.id}",
            source = "saramin",
            isFavorited = false,
        )
    }

    val notifications: List<NotificationDto> = listOf(
        NotificationDto(
            id = "ntf-20260526-001",
            sentAt = now.withHour(0).withMinute(0),
            kind = "morning_digest",
            title = "오늘 새 공고 6건 ☀️",
            body = "삼성·네이버·카카오 외 3건",
            jobIds = jobs.filter { it.kind == "NEW" }.take(6).map { it.id },
            read = false,
        ),
        NotificationDto(
            id = "ntf-20260525-002",
            sentAt = now.minusDays(1).withHour(12).withMinute(0),
            kind = "evening_digest",
            title = "마감 임박 2건 🔥",
            body = "현대자동차 신입공채 D-1, 카카오 프론트 D-3",
            jobIds = listOf("hyundai-closing", "kakao-fe-update"),
            read = true,
        ),
    )

    private fun jobOf(
        id: String,
        companyId: Long,
        title: String,
        kind: String,
        deadlineDate: LocalDate,
        location: String? = null,
        experience: String? = null,
        education: String? = null,
        tags: List<String> = emptyList(),
    ): JobDto {
        val deadline = deadlineDate.atTime(23, 59, 59).atOffset(ZoneOffset.ofHours(9))
            .withOffsetSameInstant(ZoneOffset.UTC)
        val days = ChronoUnit.DAYS.between(now.toLocalDate(), deadlineDate)
        val dday = when {
            days < 0 -> "D+${-days}"
            days == 0L -> "D-Day"
            else -> "D-$days"
        }
        return JobDto(
            id = id,
            company = companyEmbed(companyId),
            title = title,
            kind = kind,
            dday = dday,
            deadline = deadline,
            location = location,
            experience = experience,
            education = education,
            tags = tags,
            isFavorited = false,
        )
    }
}
