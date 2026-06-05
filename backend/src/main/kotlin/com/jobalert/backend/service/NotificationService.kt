package com.jobalert.backend.service

import com.jobalert.backend.dto.NotificationDto
import com.jobalert.backend.dto.NotificationHistoryResponse
import com.jobalert.backend.dto.NotificationReadResponse
import com.jobalert.backend.entity.Device
import com.jobalert.backend.entity.NotificationHistory
import com.jobalert.backend.exception.NotFoundException
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.DeviceRepository
import com.jobalert.backend.repository.JobRepository
import com.jobalert.backend.repository.NotificationHistoryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Clock
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 알림 히스토리 — 매일 9시·21시 다이제스트를 DB(notification_history)에 기록하고 조회한다.
 *
 * 실제 잠금화면 발송(FCM)과 별개로, 기록·조회(히스토리 화면)는 여기서 완결된다.
 * 발송은 FCM 키 준비 후 [FcmSender] 등으로 얹는다(이 레코드를 push로 전송 + delivered 표시).
 */
@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationHistoryRepository,
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val deviceRepository: DeviceRepository,
    private val clock: Clock,
) {
    @Transactional(readOnly = true)
    fun history(deviceId: UUID, limit: Int): NotificationHistoryResponse {
        val list = notificationRepository
            .findAllByDeviceIdOrderBySentAtDesc(deviceId, PageRequest.of(0, limit))
            .map { it.toDto() }
        return NotificationHistoryResponse(notifications = list, nextCursor = null)
    }

    fun markRead(id: String): NotificationReadResponse {
        val n = notificationRepository.findById(id).orElseThrow {
            NotFoundException("NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다.")
        }
        n.isRead = true
        n.readAt = OffsetDateTime.now(clock)
        notificationRepository.save(n)
        return NotificationReadResponse(id = id, read = true)
    }

    /**
     * 다이제스트 1건 생성·저장. kind="morning_digest"(오늘 새 공고 NEW) / "evening_digest"(마감 임박 CLOSING).
     * 실제 발송(FCM)은 별도 — 여기선 기록만(delivered=false).
     */
    fun generateDigest(deviceId: UUID, kind: String): NotificationDto {
        ensureDevice(deviceId)
        val now = OffsetDateTime.now(clock)
        val evening = kind == "evening_digest"
        val targetKind = if (evening) "CLOSING" else "NEW"

        val jobs = jobRepository.findAllByKindAndIsActiveTrue(targetKind, PageRequest.of(0, 50))
        val total = jobRepository.countByKindAndIsActiveTrue(targetKind).toInt()
        val companyById = companyRepository.findAllById(jobs.map { it.companyId }.toSet()).associateBy { it.id }
        val names = jobs.mapNotNull { companyById[it.companyId]?.name }.distinct().take(3)

        val title = if (evening) "마감 임박 ${total}건 🔥" else "오늘 새 공고 ${total}건 ☀️"
        val body = when {
            names.isEmpty() -> "새 소식을 확인해보세요"
            total > names.size -> names.joinToString("·") + " 외 ${total - names.size}건"
            else -> names.joinToString("·")
        }

        val rec = NotificationHistory(
            id = "ntf-${if (evening) "eve" else "mor"}-${deviceId.toString().take(8)}-${now.toEpochSecond()}",
            deviceId = deviceId,
            sentAt = now,
            kind = kind,
            title = title,
            body = body,
            jobIds = jobs.take(10).map { it.id },
            isRead = false,
            delivered = false,
        )
        return notificationRepository.save(rec).toDto()
    }

    private fun NotificationHistory.toDto() = NotificationDto(
        id = id,
        sentAt = sentAt,
        kind = kind,
        title = title,
        body = body,
        jobIds = jobIds,
        read = isRead,
    )

    private fun ensureDevice(deviceId: UUID) {
        if (!deviceRepository.existsById(deviceId)) {
            deviceRepository.save(Device(deviceId = deviceId, platform = "android"))
        }
    }
}
