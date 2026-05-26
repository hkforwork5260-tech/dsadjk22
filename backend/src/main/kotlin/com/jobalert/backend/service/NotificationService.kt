package com.jobalert.backend.service

import com.jobalert.backend.dto.NotificationHistoryResponse
import com.jobalert.backend.dto.NotificationReadResponse
import com.jobalert.backend.exception.NotFoundException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val mock: MockDataProvider,
) {
    private val readState = mutableMapOf<String, Boolean>()

    fun history(deviceId: UUID, limit: Int): NotificationHistoryResponse {
        val list = mock.notifications.take(limit).map { n ->
            n.copy(read = readState[n.id] ?: n.read)
        }
        return NotificationHistoryResponse(notifications = list, nextCursor = null)
    }

    fun markRead(id: String): NotificationReadResponse {
        mock.notifications.firstOrNull { it.id == id }
            ?: throw NotFoundException("NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다.")
        readState[id] = true
        return NotificationReadResponse(id = id, read = true)
    }
}
