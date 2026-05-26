package com.jobalert.backend.controller

import com.jobalert.backend.dto.NotificationHistoryResponse
import com.jobalert.backend.dto.NotificationReadResponse
import com.jobalert.backend.exception.BadRequestException
import com.jobalert.backend.service.NotificationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val notificationService: NotificationService,
) {

    @GetMapping("/history")
    fun history(
        @RequestHeader("X-Device-Id", required = false) deviceId: String?,
        @RequestParam(defaultValue = "30") limit: Int,
    ): NotificationHistoryResponse = notificationService.history(parseDeviceId(deviceId), limit)

    @PostMapping("/{id}/read")
    fun markRead(@PathVariable id: String): NotificationReadResponse =
        notificationService.markRead(id)

    private fun parseDeviceId(raw: String?): UUID {
        if (raw.isNullOrBlank()) throw BadRequestException("MISSING_DEVICE_ID", "X-Device-Id 헤더가 필요합니다.")
        return runCatching { UUID.fromString(raw) }
            .getOrElse { throw BadRequestException("INVALID_DEVICE_ID", "X-Device-Id 형식이 잘못되었습니다.") }
    }
}
