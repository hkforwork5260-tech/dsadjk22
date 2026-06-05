package com.jobalert.backend.service

import com.jobalert.backend.dto.DevicePreferencesDto
import com.jobalert.backend.dto.DevicePreferencesUpdateRequest
import com.jobalert.backend.dto.DeviceRegisterRequest
import com.jobalert.backend.dto.DeviceRegisterResponse
import com.jobalert.backend.entity.Device
import com.jobalert.backend.repository.DeviceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 기기 등록·설정. FCM 토큰을 devices 테이블에 영속(푸시 발송 시 토큰 조회에 사용).
 * 관심직군(preferences.categories)은 v0.1에선 클라이언트 로컬(ActiveFilter)이 보유 — 서버 미저장.
 */
@Service
@Transactional
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val clock: Clock,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun register(req: DeviceRegisterRequest): DeviceRegisterResponse {
        val now = OffsetDateTime.now(clock)
        val device = deviceRepository.findById(req.deviceId).orElseGet { Device(deviceId = req.deviceId) }
        device.fcmToken = req.fcmToken
        device.platform = req.platform
        device.appVersion = req.appVersion
        device.osVersion = req.osVersion
        device.pushMorning = req.preferences.pushMorning
        device.pushEvening = req.preferences.pushEvening
        device.lastSeenAt = now
        device.updatedAt = now
        deviceRepository.save(device)
        log.info("device registered: {} platform={}", req.deviceId, req.platform)
        return DeviceRegisterResponse(deviceId = req.deviceId, registeredAt = now)
    }

    fun updatePreferences(deviceId: UUID, req: DevicePreferencesUpdateRequest): DevicePreferencesDto {
        val device = deviceRepository.findById(deviceId).orElseGet { Device(deviceId = deviceId) }
        req.pushMorning?.let { device.pushMorning = it }
        req.pushEvening?.let { device.pushEvening = it }
        device.updatedAt = OffsetDateTime.now(clock)
        deviceRepository.save(device)
        return DevicePreferencesDto(
            categories = req.categories ?: emptyList(),
            favoriteCompanies = req.favoriteCompanies ?: emptyList(),
            pushMorning = device.pushMorning,
            pushEvening = device.pushEvening,
        )
    }
}
