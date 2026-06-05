package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyBriefDto
import com.jobalert.backend.dto.FavoriteCompanyItem
import com.jobalert.backend.dto.FavoritesListResponse
import com.jobalert.backend.dto.FavoriteToggleResponse
import com.jobalert.backend.entity.Device
import com.jobalert.backend.entity.UserFavorite
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.DeviceRepository
import com.jobalert.backend.repository.JobRepository
import com.jobalert.backend.repository.UserFavoriteRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 관심기업(즐겨찾기) — 로그인 없이 익명 기기ID 기준으로 user_favorites 테이블에 저장.
 *
 * 기기는 즐겨찾기 추가 시 없으면 자동 생성(FCM 등록 전에도 동작). v0.1은 인증 없음 —
 * 기기ID 헤더(X-Device-Id)가 곧 신원. 향후 로그인 도입 시 device.user_id로 승격.
 */
@Service
@Transactional
class FavoriteService(
    private val favoriteRepository: UserFavoriteRepository,
    private val deviceRepository: DeviceRepository,
    private val companyRepository: CompanyRepository,
    private val jobRepository: JobRepository,
) {
    fun add(deviceId: UUID, companyId: Long): FavoriteToggleResponse {
        ensureDevice(deviceId)
        if (!favoriteRepository.existsByDeviceIdAndCompanyId(deviceId, companyId)) {
            favoriteRepository.save(UserFavorite(deviceId = deviceId, companyId = companyId))
        }
        return FavoriteToggleResponse(favorited = true, companyId = companyId)
    }

    fun remove(deviceId: UUID, companyId: Long): FavoriteToggleResponse {
        favoriteRepository.deleteByDeviceIdAndCompanyId(deviceId, companyId)
        return FavoriteToggleResponse(favorited = false, companyId = companyId)
    }

    @Transactional(readOnly = true)
    fun list(deviceId: UUID): FavoritesListResponse {
        val favCompanyIds = favoriteRepository.findAllByDeviceId(deviceId).map { it.companyId }
        if (favCompanyIds.isEmpty()) return FavoritesListResponse(emptyList())

        val items = companyRepository.findAllById(favCompanyIds).map { c ->
            val id = c.id!!
            FavoriteCompanyItem(
                company = CompanyBriefDto(
                    id = id,
                    name = c.name,
                    logo = c.name.take(2),
                    logoUrl = c.logoUrl,
                    industry = c.industry,
                    size = c.size,
                    activeJobCount = jobRepository.countByCompanyIdAndIsActiveTrue(id).toInt(),
                    isFavorited = true,
                ),
                newCount = jobRepository.countByCompanyIdAndKindAndIsActiveTrue(id, "NEW").toInt(),
                hasAlarm = true,
            )
        }
        return FavoritesListResponse(companies = items)
    }

    /** user_favorites.device_id 는 devices FK라 기기 행이 먼저 있어야 한다. 없으면 최소 정보로 생성. */
    private fun ensureDevice(deviceId: UUID) {
        if (!deviceRepository.existsById(deviceId)) {
            deviceRepository.save(Device(deviceId = deviceId, platform = "android"))
        }
    }
}
