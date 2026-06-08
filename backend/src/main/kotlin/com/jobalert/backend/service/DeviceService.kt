package com.jobalert.backend.service

import com.jobalert.backend.dto.DevicePreferencesDto
import com.jobalert.backend.dto.DevicePreferencesUpdateRequest
import com.jobalert.backend.dto.DeviceRegisterRequest
import com.jobalert.backend.dto.DeviceRegisterResponse
import com.jobalert.backend.entity.Device
import com.jobalert.backend.entity.DeviceCategory
import com.jobalert.backend.repository.DeviceCategoryRepository
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
    private val deviceCategoryRepository: DeviceCategoryRepository,
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
        device.interestSizes = req.preferences.sizes.distinct().filter { it.isNotBlank() }
            .joinToString(",").ifBlank { null }
        device.lastSeenAt = now
        device.updatedAt = now
        deviceRepository.save(device)

        // 관심 직군 저장(개인화 다이제스트용) — 기존 것 비우고 새로 세팅.
        deviceCategoryRepository.deleteByDeviceId(req.deviceId)
        req.preferences.categories.distinct().forEach {
            deviceCategoryRepository.save(DeviceCategory(deviceId = req.deviceId, categoryCode = it, createdAt = now))
        }

        log.info("device registered: {} platform={} categories={}", req.deviceId, req.platform, req.preferences.categories)
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
