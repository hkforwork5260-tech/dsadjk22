package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "notification_history")
class NotificationHistory(
    @Id
    @Column(length = 64)
    var id: String = "",

    @Column(name = "device_id", nullable = false)
    var deviceId: UUID = UUID.randomUUID(),

    @Column(name = "sent_at", nullable = false)
    var sentAt: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false, length = 32)
    var kind: String = "morning_digest",

    @Column(nullable = false, length = 255)
    var title: String = "",

    @Column(nullable = false, columnDefinition = "text")
    var body: String = "",

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "job_ids", nullable = false, columnDefinition = "jsonb")
    var jobIds: List<String> = emptyList(),

    @Column(name = "is_read", nullable = false)
    var isRead: Boolean = false,

    @Column(name = "read_at")
    var readAt: OffsetDateTime? = null,

    @Column(nullable = false)
    var delivered: Boolean = false,

    @Column(name = "fcm_message_id", length = 255)
    var fcmMessageId: String? = null,
)
