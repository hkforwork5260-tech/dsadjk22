package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "devices")
class Device(
    @Id
    @Column(name = "device_id")
    var deviceId: UUID = UUID.randomUUID(),

    @Column(name = "user_id")
    var userId: Long? = null,

    @Column(name = "fcm_token", columnDefinition = "text")
    var fcmToken: String? = null,

    @Column(nullable = false, length = 16)
    var platform: String = "android",

    @Column(name = "app_version", length = 32)
    var appVersion: String? = null,

    @Column(name = "os_version", length = 32)
    var osVersion: String? = null,

    @Column(name = "push_morning", nullable = false)
    var pushMorning: Boolean = true,

    @Column(name = "push_evening", nullable = false)
    var pushEvening: Boolean = true,

    @Column(name = "last_seen_at", nullable = false)
    var lastSeenAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime = OffsetDateTime.now(),
)
