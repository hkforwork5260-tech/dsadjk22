package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime

@Entity
@Table(name = "jobs")
class Job(
    @Id
    @Column(length = 64)
    var id: String = "",

    @Column(name = "company_id", nullable = false)
    var companyId: Long = 0,

    @Column(nullable = false, length = 32)
    var source: String = "",

    @Column(name = "source_external_id", nullable = false, length = 128)
    var sourceExternalId: String = "",

    @Column(nullable = false, length = 500)
    var title: String = "",

    @Column(nullable = false, length = 16)
    var kind: String = "NEW",

    var location: String? = null,
    var experience: String? = null,
    var education: String? = null,
    var salary: String? = null,

    @Column(name = "posting_date")
    var postingDate: OffsetDateTime? = null,

    var deadline: OffsetDateTime? = null,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(columnDefinition = "text")
    var summary: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var preferred: List<String>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var process: List<String>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    var tags: List<String>? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "job_category_codes", columnDefinition = "jsonb")
    var jobCategoryCodes: List<String>? = null,

    @Column(name = "original_url", columnDefinition = "text")
    var originalUrl: String? = null,

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = true,

    @Column(name = "first_seen_at", nullable = false, updatable = false)
    var firstSeenAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "last_seen_at", nullable = false)
    var lastSeenAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "closed_at")
    var closedAt: OffsetDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
