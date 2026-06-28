package com.jobalert.backend.repository

import com.jobalert.backend.entity.Company
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findByNameNormalized(nameNormalized: String): Company?
    fun existsByNameNormalized(nameNormalized: String): Boolean
    fun findAllByIsApprovedTrue(): List<Company>

    /**
     * 회사명 부분일치 검색 — 진행중 공고가 1건 이상인 회사만(죽은/빈 회사 제외).
     * 관심기업 추가 시 회사를 직접 찾기 위한 용도. 정렬·개수는 서비스에서 활성 공고수 기준으로 처리.
     */
    @Query(
        """
        SELECT c FROM Company c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
          AND EXISTS (SELECT 1 FROM Job j WHERE j.companyId = c.id AND j.isActive = true)
        """,
    )
    fun searchByName(@Param("q") q: String, pageable: Pageable): List<Company>
}
