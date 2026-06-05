package com.jobalert.backend.repository

import com.jobalert.backend.entity.Job
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface JobRepository : JpaRepository<Job, String> {

    fun findBySourceAndSourceExternalId(source: String, sourceExternalId: String): Job?

    /** 만료 스윕용: 특정 소스의 현재 활성 공고 전부. 이번 수집에서 안 보이면 닫는다. */
    fun findAllBySourceAndIsActiveTrue(source: String): List<Job>

    fun findAllByIsActiveTrueOrderByFirstSeenAtDesc(pageable: Pageable): List<Job>

    fun findAllByKindAndIsActiveTrue(kind: String, pageable: Pageable): List<Job>

    @Query("""
        SELECT j FROM Job j
        WHERE j.isActive = true
          AND j.deadline IS NOT NULL
          AND j.deadline BETWEEN :from AND :to
        ORDER BY j.deadline ASC
    """)
    fun findUpcoming(@Param("from") from: OffsetDateTime, @Param("to") to: OffsetDateTime): List<Job>

    @Query("""
        SELECT j FROM Job j, Company c
        WHERE j.companyId = c.id
          AND j.isActive = true
          AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%')))
        ORDER BY j.firstSeenAt DESC
    """)
    fun searchByKeyword(@Param("q") q: String, pageable: Pageable): List<Job>

    fun countByKindAndIsActiveTrue(kind: String): Long

    fun countByCompanyIdAndIsActiveTrue(companyId: Long): Long

    fun countByCompanyIdAndKindAndIsActiveTrue(companyId: Long, kind: String): Long

    fun findAllByCompanyIdAndIsActiveTrueOrderByFirstSeenAtDesc(companyId: Long, pageable: Pageable): List<Job>

    fun findAllByCompanyIdAndKindAndIsActiveTrue(companyId: Long, kind: String, pageable: Pageable): List<Job>

    /** 마감(비활성) 공고 — 회사 상세의 "최근 채용 이력"용. */
    fun findAllByCompanyIdAndIsActiveFalseOrderByClosedAtDesc(companyId: Long, pageable: Pageable): List<Job>

    /** 비슷한 공고: 같은 업종(회사 industry)의 다른 active 공고. */
    @Query("""
        SELECT j FROM Job j, Company c
        WHERE j.companyId = c.id
          AND c.industry = :industry
          AND j.isActive = true
          AND j.id <> :excludeId
        ORDER BY j.firstSeenAt DESC
    """)
    fun findSimilarByIndustry(
        @Param("industry") industry: String,
        @Param("excludeId") excludeId: String,
        pageable: Pageable,
    ): List<Job>
}
