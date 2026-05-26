package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "api_call_log")
class ApiCallLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, length = 32)
    var source: String = "",

    @Column(length = 255)
    var endpoint: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_params", columnDefinition = "jsonb")
    var requestParams: Map<String, Any?>? = null,

    @Column(name = "status_code")
    var statusCode: Int? = null,

    @Column(name = "response_size_bytes")
    var responseSizeBytes: Int? = null,

    @Column(name = "duration_ms")
    var durationMs: Int? = null,

    @Column(name = "error_message", columnDefinition = "text")
    var errorMessage: String? = null,

    @Column(name = "called_at", nullable = false, updatable = false)
    var calledAt: OffsetDateTime = OffsetDateTime.now(),
)
