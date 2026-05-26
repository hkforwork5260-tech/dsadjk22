package com.jobalert.backend.service

import com.jobalert.backend.dto.CompanyListResponse
import com.jobalert.backend.dto.FavoriteToggleResponse
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * v0.1 mock: 메모리에 device → company id set 보관. Phase 3에서 user_favorites DB.
 */
@Service
class FavoriteService(
    private val mock: MockDataProvider,
) {
    private val store = ConcurrentHashMap<UUID, MutableSet<Long>>()

    fun add(deviceId: UUID, companyId: Long): FavoriteToggleResponse {
        val set = store.computeIfAbsent(deviceId) { mutableSetOf() }
        set.add(companyId)
        return FavoriteToggleResponse(favorited = true, companyId = companyId)
    }

    fun remove(deviceId: UUID, companyId: Long): FavoriteToggleResponse {
        store[deviceId]?.remove(companyId)
        return FavoriteToggleResponse(favorited = false, companyId = companyId)
    }

    fun list(deviceId: UUID): CompanyListResponse {
        val ids = store[deviceId] ?: emptySet()
        val favs = mock.companies.filter { it.id in ids }
            .map { it.copy(isFavorited = true) }
        return CompanyListResponse(companies = favs)
    }
}
