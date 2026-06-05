package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 기기 ↔ 관심 직군 (device_categories). 복합 PK (device_id, category_code).
 * 개인화 다이제스트("내 직군 새 공고 N건")의 근거. category_code는 job_categories FK.
 */
@Entity
@Table(name = "device_categories")
@IdClass(DeviceCategoryId::class)
class DeviceCategory(
    @Id
    @Column(name = "device_id")
    var deviceId: UUID = UUID.randomUUID(),

    @Id
    @Column(name = "category_code", length = 64)
    var categoryCode: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
)

data class DeviceCategoryId(
    var deviceId: UUID = UUID.randomUUID(),
    var categoryCode: String = "",
) : Serializable
