package com.jobalert.backend.dto

import java.time.OffsetDateTime

data class NotificationDto(
    val id: String,
    val sentAt: OffsetDateTime,
    val kind: String,
    val title: String,
    val body: String,
    val jobIds: List<String>,
    val read: Boolean,
)

data class NotificationHistoryResponse(
    val notifications: List<NotificationDto>,
    val nextCursor: String? = null,
)

data class NotificationReadResponse(
    val id: String,
    val read: Boolean,
)
