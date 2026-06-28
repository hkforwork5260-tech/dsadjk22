package com.jobalert.backend.client.source.greeting

import com.jobalert.backend.client.source.GreetingWorkspace
import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.SourceUtil

/**
 * 그리팅 openings 응답 → [RawJobPosting] 매퍼 (순수 함수, 단위 테스트 용이).
 *
 * 회사명은 응답 group.name(노이즈 많음) 대신 워크스페이스 displayName을 쓴다.
 * 한 공고에 여러 직무(openingJobPositions)가 묶일 수 있어 직군·근무지·경력은 첫 유효값을 취한다.
 */
object GreetingMapper {

    /** careerType 코드 → 한국어 경력구분. 모르면 null. */
    private fun careerKorean(code: String?): String? = when (code?.uppercase()) {
        "NEW_COMER" -> "신입"
        "EXPERIENCED" -> "경력"
        "NOT_MATTER" -> "경력무관"
        else -> null
    }

    /**
     * 경력구분 라벨. 여러 직무의 careerType을 합치고(신입·경력 등), 없으면 인턴 고용형태만 보조 표기.
     */
    private fun experienceLabel(positions: List<GreetingJobPosition>): String? {
        val labels = positions.mapNotNull { careerKorean(it.jobPositionCareer?.careerType) }.distinct()
        if (labels.isNotEmpty()) return labels.joinToString("·")
        if (positions.any { it.jobPositionEmployment?.employmentType?.uppercase() == "INTERN_WORKER" }) return "인턴"
        return null
    }

    private fun firstNonBlank(values: Sequence<String?>): String? =
        values.firstOrNull { !it.isNullOrBlank() }?.trim()

    fun toRawJob(ws: GreetingWorkspace, o: GreetingOpening): RawJobPosting? {
        val oid = o.openingId ?: return null
        val title = o.title?.trim()?.takeIf { it.isNotBlank() } ?: return null

        val positions = o.openingJobPosition?.openingJobPositions ?: emptyList()
        val occupation = firstNonBlank(positions.asSequence().map { it.workspaceOccupation?.occupation })
        val job = firstNonBlank(positions.asSequence().map { it.workspaceJob?.job })
        val place = firstNonBlank(positions.asSequence().map { it.workspacePlace?.place })
        val division = o.workspaceDivision?.division?.trim()?.takeIf { it.isNotBlank() }

        val keywords = listOfNotNull(occupation, job, division).distinct()

        return RawJobPosting(
            source = "greeting",
            externalId = "greeting-${ws.workspaceId}-$oid",
            title = title,
            companyName = ws.displayName,
            companyHomepage = ws.homepage,
            location = place ?: "한국",
            department = occupation ?: job,
            experience = experienceLabel(positions),
            postingDateEpoch = SourceUtil.isoToEpochSeconds(o.openDate),
            deadlineEpoch = SourceUtil.isoToEpochSeconds(o.dueDate),
            originalUrl = "https://${ws.originHost ?: "${ws.subdomain}.career.greetinghr.com"}/o/$oid",
            keywords = keywords,
        )
    }
}
