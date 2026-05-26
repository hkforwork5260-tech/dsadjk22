package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "company_aliases")
class CompanyAlias(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "company_id", nullable = false)
    var companyId: Long = 0,

    @Column(nullable = false)
    var alias: String = "",

    @Column(name = "alias_normalized", nullable = false, unique = true)
    var aliasNormalized: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
)
