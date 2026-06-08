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
import com.jobalert.backend.repository.DeviceCategoryRepository
import com.jobalert.backend.repository.NotificationHistoryRepository
import org.slf4j.LoggerFactory
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
    private val deviceCategoryRepository: DeviceCategoryRepository,
    private val fcmSender: FcmSender,
    private val clock: Clock,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 등록된 모든 기기에 다이제스트 발송(매일 9시·21시 스케줄러가 호출).
     * 기기별 push 설정(pushMorning/pushEvening) 존중. v0.1은 전역 다이제스트(직군 개인화는 추후).
     */
    fun sendDailyDigestToAll(kind: String): Int {
        val morning = kind != "evening_digest"
        val targets = deviceRepository.findAllByFcmTokenIsNotNull()
            .filter { if (morning) it.pushMorning else it.pushEvening }
        targets.forEach { generateDigest(it.deviceId, kind) }
        log.info("daily digest 발송: kind={} 대상기기={}", kind, targets.size)
        return targets.size
    }

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

        // 기기의 관심 직군. 있으면 그 직군만, 없으면 전체(전역 다이제스트).
        val myCategories = deviceCategoryRepository.findAllByDeviceId(deviceId).map { it.categoryCode }.toSet()
        val personalized = myCategories.isNotEmpty()

        val pool = jobRepository.findAllByKindAndIsActiveTrue(targetKind, PageRequest.of(0, 2000))
        val jobs = if (personalized) {
            pool.filter { j -> j.jobCategoryCodes?.any { it in myCategories } == true }
        } else {
            pool
        }
        val total = jobs.size
        val companyById = companyRepository.findAllById(jobs.map { it.companyId }.toSet()).associateBy { it.id }
        val names = jobs.mapNotNull { companyById[it.companyId]?.name }.distinct().take(3)

        val (title, body) = buildDigestMessage(evening, total, names, now)

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

        // 실제 잠금화면 푸시 — 기기에 FCM 토큰이 있고 FCM 활성일 때만(best-effort).
        val token = deviceRepository.findById(deviceId).orElse(null)?.fcmToken
        if (!token.isNullOrBlank()) {
            fcmSender.sendToToken(token, title, body)?.let { msgId ->
                rec.delivered = true
                rec.fcmMessageId = msgId
            }
        }
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

    /**
     * 듀오링고풍 간단·친근 다이제스트 문구. 매일(dayOfYear) 다른 템플릿을 돌려 신선하게.
     * title은 "새 공고 N개" 같은 한 줄 후크, body는 회사명 몇 개로 살짝 호기심.
     */
    private fun buildDigestMessage(evening: Boolean, total: Int, names: List<String>, now: OffsetDateTime): Pair<String, String> {
        val idx = now.dayOfYear
        if (total == 0) {
            val title = EMPTY_TITLES[idx % EMPTY_TITLES.size]
            val body = if (evening) "오늘 마감하는 공고는 없어요" else "내일 더 좋은 소식으로 올게요 🙌"
            return title to body
        }
        val titles = if (evening) EVENING_TITLES else MORNING_TITLES
        val title = String.format(titles[idx % titles.size], total)
        val body = when {
            names.isEmpty() -> "지금 1분만 확인해요 👀"
            total > names.size -> names.joinToString("·") + " 외 ${total - names.size}건"
            else -> names.joinToString("·")
        }
        return title to body
    }

    companion object {
        // 아침(새 공고) — %d = 건수. 매일 돌려가며 노출.
        private val MORNING_TITLES = listOf(
            "오늘 새 공고 %d개 떴어요 👀",
            "단이가 새 공고 %d개 찾았어요 🐱",
            "따끈한 새 공고 %d개 도착 ☀️",
            "새 공고 %d개! 1분이면 충분해요",
            "잠깐! 새 공고 %d개 있어요 ✨",
        )
        // 저녁(마감 임박)
        private val EVENING_TITLES = listOf(
            "마감 임박 %d개 ⏰ 놓치지 마요",
            "오늘 마감 %d개, 지금 확인해요 🔥",
            "단이가 챙긴 마감 임박 %d개 🐱",
            "곧 마감! %d개 서둘러요 ⏳",
        )
        // 새 소식 없는 날
        private val EMPTY_TITLES = listOf(
            "오늘은 조용하네요 😴",
            "새 소식은 없지만 단이는 지켜보는 중 🐱",
            "잠깐 쉬어가요. 새 공고 없어요 🍵",
        )
    }
}
