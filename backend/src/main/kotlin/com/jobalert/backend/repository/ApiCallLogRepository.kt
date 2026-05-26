package com.jobalert.backend.repository

import com.jobalert.backend.entity.ApiCallLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
interface ApiCallLogRepository : JpaRepository<ApiCallLog, Long> {

    @Query("SELECT COUNT(l) FROM ApiCallLog l WHERE l.source = :source AND l.calledAt >= :since")
    fun countSince(@Param("source") source: String, @Param("since") since: OffsetDateTime): Long
}
