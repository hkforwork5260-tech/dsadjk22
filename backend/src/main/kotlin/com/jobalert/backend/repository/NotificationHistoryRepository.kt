package com.jobalert.backend.repository

import com.jobalert.backend.entity.NotificationHistory
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface NotificationHistoryRepository : JpaRepository<NotificationHistory, String> {
    fun findAllByDeviceIdOrderBySentAtDesc(deviceId: UUID, pageable: Pageable): List<NotificationHistory>
    fun countByDeviceIdAndIsReadFalse(deviceId: UUID): Long
}
