package com.jobalert.backend.client.source.recruiter

import com.jobalert.backend.client.source.RawJobPosting
import com.jobalert.backend.client.source.RecruiterTenant
import com.jobalert.backend.client.source.SourceUtil

/**
 * recruiter jobflex 응답 → [RawJobPosting] 매퍼 (순수 함수, 단위 테스트 용이).
 */
object RecruiterMapper {

    /** careerType 코드 → 한국어 경력구분. 모르면 null. */
    private fun careerTypeKorean(code: String?): String? = when (code?.uppercase()) {
        "NEW" -> "신입"
        "CAREER" -> "경력"
        "NEW_CAREER" -> "신입·경력"
        "INTERN" -> "인턴"
        "CONTRACT" -> "계약직"
        else -> null
    }

    /** 경력구분·고용형태 라벨인지(회사명으로 쓰면 안 되는 값 배제). */
    private fun isExperienceTag(name: String?): Boolean {
        if (name == null) return false
        return listOf("신입", "경력", "인턴", "계약", "정규", "무관", "전환형", "체험형").any { name.contains(it) }
    }

    private val BRACKET_PREFIX = Regex("""^\s*[\[(]([^\])]+)[\])]""")

    /** 제목 앞 "[계열사] ..." 또는 "(계열사) ..."에서 계열사명 추출. 없으면 null. */
    private fun bracketCompany(title: String): String? =
        BRACKET_PREFIX.find(title)?.groupValues?.get(1)?.trim()?.takeIf { it.isNotBlank() }

    fun toRawJob(tenant: RecruiterTenant, p: RecruiterPosition): RawJobPosting? {
        val sn = p.positionSn ?: return null
        val title = p.title?.trim()?.takeIf { it.isNotBlank() } ?: return null

        // 회사명: 그룹허브면 제목 [계열사] 접두 우선 → classificationCode(경력라벨 아닐 때) → 그룹명.
        // (careerhyundai·spc 등 일부 허브는 classificationCode가 경력구분이라 계열사명이 아님 → 제목 접두가 안정적.)
        val company = if (tenant.groupHub) {
            bracketCompany(title)
                ?: p.classificationCode?.trim()?.takeIf { it.isNotBlank() && !isExperienceTag(it) }
                ?: tenant.displayName
        } else {
            tenant.displayName
        }

        // 경력구분: 태그(신입/경력/인턴…) 우선, 없으면 careerType 매핑, 그래도 없고 단일사면 classificationCode.
        val experience = p.tagList.map { it.tagName }.firstOrNull { isExperienceTag(it) }
            ?: careerTypeKorean(p.careerType)
            ?: p.classificationCode?.takeIf { !tenant.groupHub && isExperienceTag(it) }

        return RawJobPosting(
            source = "recruiter",
            externalId = "recruiter-${tenant.tenant}-$sn",
            title = title,
            companyName = company,
            companyHomepage = tenant.homepage,
            location = "한국",
            experience = experience,
            postingDateEpoch = SourceUtil.localKstToEpochSeconds(p.startDateTime),
            deadlineEpoch = SourceUtil.localKstToEpochSeconds(p.endDateTime),
            originalUrl = "https://${tenant.tenant}.recruiter.co.kr/career/home?positionSn=$sn",
            keywords = p.tagList.mapNotNull { it.tagName?.trim() }.filter { it.isNotBlank() },
        )
    }
}
