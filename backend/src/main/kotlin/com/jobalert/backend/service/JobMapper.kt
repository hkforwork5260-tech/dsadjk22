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
            size = company?.size,
        )

    /**
     * 목록 응답 경량화 범위. 화면 카드가 안 쓰는 무거운 필드를 비워 응답 크기를 줄인다(전체를 빠짐없이
     * 받아도 빠르게). 상세는 항상 [toDetailDto]로 전문을 받으므로 목록 경량화와 무관.
     *  - FULL  : 모든 필드(찾아보기 reels 카드는 본문·급여·태그·학력 다 씀 → 풀 필드 필수)
     *  - TODAY : 홈 카드는 회사·제목·D-day + 지역명(location)·신입접두(experience)만 씀 → 본문·태그·직군·급여·학력 제거
     *  - SEARCH: 검색 카드는 회사·제목·D-day만 → 위 + location·experience까지 제거
     */
    enum class DtoScope { FULL, TODAY, SEARCH }

    fun toDto(job: Job, company: Company?, isFavorited: Boolean = false, scope: DtoScope = DtoScope.FULL): JobDto {
        val light = scope != DtoScope.FULL
        return JobDto(
            id = job.id,
            company = toCompanyEmbed(job.companyId, company),
            title = job.title,
            kind = job.kind,
            dday = dday(job.deadline),
            deadline = job.deadline,
            // 홈은 로고 자리 지역명(regionShort)에 location 사용 → TODAY는 유지, SEARCH는 제거.
            location = if (scope == DtoScope.SEARCH) null else job.location,
            // 홈은 제목 "(신입)" 접두(displayRole)에 experience 사용 → TODAY 유지, SEARCH 제거.
            experience = if (scope == DtoScope.SEARCH) null else job.experience,
            education = if (light) null else job.education,
            tags = if (light) emptyList() else (job.tags ?: emptyList()),
            jobCategories = if (light) emptyList() else (job.jobCategoryCodes ?: emptyList()),
            salary = if (light) null else job.salary,
            // 본문은 찾아보기(FULL)만 미리보기(160자) 필요. 홈·검색 목록 카드는 본문을 안 써 제거 → 응답 대폭 경량화.
            // 전체 본문은 상세(toDetailDto)에서 별도로 받는다.
            description = if (light) null else job.description?.take(160),
            isFavorited = isFavorited,
        )
    }

    fun toDetailDto(job: Job, company: Company?, isFavorited: Boolean = false, isSaved: Boolean = false): JobDetailDto =
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
            isSaved = isSaved,
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
