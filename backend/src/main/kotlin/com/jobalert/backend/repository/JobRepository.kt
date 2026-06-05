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
        SELECT j FROM Job j
        WHERE j.isActive = true
          AND (LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    fun searchByKeyword(@Param("q") q: String, pageable: Pageable): List<Job>

    fun countByKindAndIsActiveTrue(kind: String): Long
}
