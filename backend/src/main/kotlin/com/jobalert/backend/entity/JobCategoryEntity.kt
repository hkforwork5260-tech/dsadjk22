package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "job_categories")
class JobCategoryEntity(
    @Id
    @Column(length = 64)
    var code: String = "",

    @Column(nullable = false)
    var label: String = "",

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,
)
