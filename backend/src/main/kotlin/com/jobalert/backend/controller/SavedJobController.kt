package com.jobalert.backend.controller

import com.jobalert.backend.dto.SaveToggleResponse
import com.jobalert.backend.dto.SavedJobsResponse
import com.jobalert.backend.exception.BadRequestException
import com.jobalert.backend.service.SavedJobService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/users/me/saved")
class SavedJobController(
    private val savedJobService: SavedJobService,
) {

    @GetMapping
    fun list(@RequestHeader("X-Device-Id", required = false) deviceId: String?): SavedJobsResponse =
        savedJobService.list(parseDeviceId(deviceId))

    @PostMapping("/{jobId}")
    fun add(
        @RequestHeader("X-Device-Id", required = false) deviceId: String?,
        @PathVariable jobId: String,
    ): SaveToggleResponse = savedJobService.add(parseDeviceId(deviceId), jobId)

    @DeleteMapping("/{jobId}")
    fun remove(
        @RequestHeader("X-Device-Id", required = false) deviceId: String?,
        @PathVariable jobId: String,
    ): SaveToggleResponse = savedJobService.remove(parseDeviceId(deviceId), jobId)

    private fun parseDeviceId(raw: String?): UUID {
        if (raw.isNullOrBlank()) throw BadRequestException("MISSING_DEVICE_ID", "X-Device-Id 헤더가 필요합니다.")
        return runCatching { UUID.fromString(raw) }
            .getOrElse { throw BadRequestException("INVALID_DEVICE_ID", "X-Device-Id 형식이 잘못되었습니다.") }
    }
}
