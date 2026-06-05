package com.jobalert.backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import java.time.OffsetDateTime
import java.util.UUID

data class DevicePreferencesDto(
    val categories: List<String> = emptyList(),
    val favoriteCompanies: List<Long> = emptyList(),
    val pushMorning: Boolean = true,
    val pushEvening: Boolean = true,
)

data class DeviceRegisterRequest(
    @field:NotBlank val fcmToken: String,
    // 기본값 — 클라이언트(kotlinx)가 기본값 'android'를 JSON에서 생략해도 동작.
    @field:Pattern(regexp = "android|ios") val platform: String = "android",
    val deviceId: UUID,
    val appVersion: String? = null,
    val osVersion: String? = null,
    val preferences: DevicePreferencesDto = DevicePreferencesDto(),
)

data class DeviceRegisterResponse(
    val deviceId: UUID,
    val registeredAt: OffsetDateTime,
)

data class DevicePreferencesUpdateRequest(
    val categories: List<String>? = null,
    val favoriteCompanies: List<Long>? = null,
    val pushMorning: Boolean? = null,
    val pushEvening: Boolean? = null,
)

data class FavoriteToggleResponse(
    val favorited: Boolean,
    val companyId: Long,
)
