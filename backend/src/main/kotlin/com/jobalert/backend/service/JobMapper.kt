package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyDto
import com.jobalert.backend.dto.CompanyEmbedDto
import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobDto
import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.Job
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * 엔티티(Job·Company) → API DTO 변환 한 곳 모음.
 *
 * JobService·CompanyService가 똑같이 회사 임베드·D-day 계산을 하게 두면 어긋나므로
 * 변환을 여기로 모은다. D-day는 사용자 노출 시점에만 쓰는 파생값이라 DB에 저장하지 않고 매 응답 계산.
 */
@Component
class JobMapper(
    private val clock: Clock,
) {
    private val kst = ZoneId.of("Asia/Seoul")

    /** 회사가 null(삭제 등)이어도 화면이 깨지지 않게 방어적 임베드. */
    fun toCompanyEmbed(companyId: Long, company: Company?): CompanyEmbedDto =
        CompanyEmbedDto(
            id = company?.id ?: companyId,
            name = company?.name ?: "(알 수 없음)",
            logo = (company?.name ?: "?").take(2),
            logoUrl = company?.logoUrl,
            industry = company?.industry,
        )

    fun toDto(job: Job, company: Company?, isFavorited: Boolean = false): JobDto =
        JobDto(
            id = job.id,
            company = toCompanyEmbed(job.companyId, company),
            title = job.title,
            kind = job.kind,
            dday = dday(job.deadline),
            deadline = job.deadline,
            location = job.location,
            experience = job.experience,
            education = job.education,
            tags = job.tags ?: emptyList(),
            isFavorited = isFavorited,
        )

    fun toDetailDto(job: Job, company: Company?, isFavorited: Boolean = false): JobDetailDto =
        JobDetailDto(
            id = job.id,
            company = toCompanyEmbed(job.companyId, company),
            title = job.title,
            kind = job.kind,
            dday = dday(job.deadline),
            deadline = job.deadline,
            postingDate = job.postingDate,
            location = job.location,
            experience = job.experience,
            education = job.education,
            salary = job.salary,
            // 직군 분류는 수집 단계에서 아직 안 붙임(Phase 3 분류 작업) → 코드 있으면 그대로, 없으면 빈 리스트.
            jobCategories = job.jobCategoryCodes ?: emptyList(),
            tags = job.tags ?: emptyList(),
            description = job.description,
            preferred = job.preferred ?: emptyList(),
            process = job.process ?: emptyList(),
            originalUrl = job.originalUrl,
            source = job.source,
            isFavorited = isFavorited,
        )

    fun toCompanyDto(company: Company, activeJobCount: Int, isFavorited: Boolean = false): CompanyDto =
        CompanyDto(
            id = company.id!!,
            name = company.name,
            nameNormalized = company.nameNormalized,
            logoUrl = company.logoUrl,
            industry = company.industry,
            group = company.groupName,
            size = company.size,
            homepageUrl = company.homepageUrl,
            careersUrl = company.careersUrl,
            activeJobCount = activeJobCount,
            isFavorited = isFavorited,
        )

    /** 마감일까지 남은 일수 라벨. 마감일 없으면(상시채용) "상시". */
    fun dday(deadline: OffsetDateTime?): String {
        if (deadline == null) return "상시"
        val today = OffsetDateTime.now(clock).atZoneSameInstant(kst).toLocalDate()
        val deadlineDate = deadline.atZoneSameInstant(kst).toLocalDate()
        val days = ChronoUnit.DAYS.between(today, deadlineDate)
        return when {
            days < 0 -> "마감"
            days == 0L -> "D-Day"
            else -> "D-$days"
        }
    }
}
