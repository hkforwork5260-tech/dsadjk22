package com.jobalert.backend.service

import com.jobalert.backend.repository.CompanyAliasRepository
import com.jobalert.backend.repository.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사람인 응답의 회사명을 우리 1000개 회사 풀에 매칭.
 *
 * 매칭 순서:
 *  1. [CompanyNameNormalizer]로 정규화 → companies.name_normalized exact match
 *  2. 미스 → company_aliases.alias_normalized lookup
 *  3. 그래도 미스 → null (호출자는 새 회사 후보로 admin 검토용 로그)
 *
 * fuzzy 매칭(편집거리 등)은 v0.5+ 검토.
 */
@Service
@Transactional(readOnly = true)
class CompanyMatcher(
    private val companyRepository: CompanyRepository,
    private val companyAliasRepository: CompanyAliasRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /** 매칭 성공 시 companyId, 실패 시 null. */
    fun matchOrNull(rawName: String?): Long? {
        val normalized = CompanyNameNormalizer.normalize(rawName)
        if (normalized.isBlank()) return null

        companyRepository.findByNameNormalized(normalized)?.let { return it.id }
        companyAliasRepository.findByAliasNormalized(normalized)?.let { return it.companyId }

        log.debug("company match miss: raw='{}' normalized='{}'", rawName, normalized)
        return null
    }

    /** 회사명 배치를 한 번에 정규화 + 매칭. (Phase 3에서 수집된 공고 묶음 처리용) */
    fun matchBatch(rawNames: Collection<String>): MatchSummary {
        val matched = mutableMapOf<String, Long>()
        val unmatched = mutableListOf<String>()
        rawNames.distinct().forEach { raw ->
            val id = matchOrNull(raw)
            if (id != null) matched[raw] = id else unmatched += raw
        }
        log.info("company match batch: total={} matched={} unmatched={}", rawNames.size, matched.size, unmatched.size)
        return MatchSummary(matched = matched, unmatched = unmatched)
    }

    data class MatchSummary(
        val matched: Map<String, Long>,
        val unmatched: List<String>,
    )
}
