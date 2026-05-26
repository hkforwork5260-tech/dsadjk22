package com.jobalert.backend.controller

import com.jobalert.backend.dto.DevicePreferencesDto
import com.jobalert.backend.dto.DevicePreferencesUpdateRequest
import com.jobalert.backend.dto.DeviceRegisterRequest
import com.jobalert.backend.dto.DeviceRegisterResponse
import com.jobalert.backend.service.DeviceService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/devices")
class DeviceController(
    private val deviceService: DeviceService,
) {

    @PostMapping("/register")
    fun register(@RequestBody @Valid req: DeviceRegisterRequest): DeviceRegisterResponse =
        deviceService.register(req)

    @PatchMapping("/{deviceId}/preferences")
    fun updatePreferences(
        @PathVariable deviceId: UUID,
        @RequestBody req: DevicePreferencesUpdateRequest,
    ): DevicePreferencesDto = deviceService.updatePreferences(deviceId, req)
}
