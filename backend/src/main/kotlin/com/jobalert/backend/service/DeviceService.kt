package com.jobalert.backend.service

import com.jobalert.backend.dto.DevicePreferencesDto
import com.jobalert.backend.dto.DevicePreferencesUpdateRequest
import com.jobalert.backend.dto.DeviceRegisterRequest
import com.jobalert.backend.dto.DeviceRegisterResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * v0.1 mock: 메모리에 등록 보관. Phase 3에서 DB 영속화.
 */
@Service
class DeviceService {
    private val log = LoggerFactory.getLogger(javaClass)
    private val store = ConcurrentHashMap<UUID, DevicePreferencesDto>()

    fun register(req: DeviceRegisterRequest): DeviceRegisterResponse {
        store[req.deviceId] = req.preferences
        log.info("Device registered: {} platform={} categories={}",
            req.deviceId, req.platform, req.preferences.categories)
        return DeviceRegisterResponse(deviceId = req.deviceId, registeredAt = OffsetDateTime.now())
    }

    fun updatePreferences(deviceId: UUID, req: DevicePreferencesUpdateRequest): DevicePreferencesDto {
        val current = store[deviceId] ?: DevicePreferencesDto()
        val updated = current.copy(
            categories = req.categories ?: current.categories,
            favoriteCompanies = req.favoriteCompanies ?: current.favoriteCompanies,
            pushMorning = req.pushMorning ?: current.pushMorning,
            pushEvening = req.pushEvening ?: current.pushEvening,
        )
        store[deviceId] = updated
        return updated
    }
}
