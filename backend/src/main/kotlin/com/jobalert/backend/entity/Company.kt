package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "companies")
class Company(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(name = "name_normalized", nullable = false, unique = true)
    var nameNormalized: String = "",

    var industry: String? = null,

    @Column(name = "group_name")
    var groupName: String? = null,

    var size: String? = null,

    var domain: String? = null,

    @Column(name = "homepage_url", columnDefinition = "text")
    var homepageUrl: String? = null,

    @Column(name = "careers_url", columnDefinition = "text")
    var careersUrl: String? = null,

    @Column(name = "logo_url", columnDefinition = "text")
    var logoUrl: String? = null,

    @Column(columnDefinition = "text")
    var description: String? = null,

    @Column(name = "is_approved", nullable = false)
    var isApproved: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
