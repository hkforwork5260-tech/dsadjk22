package com.jobalert.backend.scheduler

import com.jobalert.backend.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 매일 고정 시간 푸시 — 듀오링고식 다이제스트.
 *  - 아침 09:00 KST(=00:00 UTC): "오늘 새 공고 N건 ☀️" (NEW)
 *  - 저녁 21:00 KST(=12:00 UTC): "마감 임박 M건 🔥" (CLOSING)
 *
 * 활성화: jobalert.push.enabled=true (+ FCM_ENABLED=true·서비스계정 키). cron은 application.yml.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.push.enabled"], havingValue = "true")
class PushScheduler(
    private val notificationService: NotificationService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${jobalert.push.morning-cron}", zone = "UTC")
    fun morning() {
        log.info("PushScheduler.morning 트리거")
        notificationService.sendDailyDigestToAll("morning_digest")
    }

    @Scheduled(cron = "\${jobalert.push.evening-cron}", zone = "UTC")
    fun evening() {
        log.info("PushScheduler.evening 트리거")
        notificationService.sendDailyDigestToAll("evening_digest")
    }
}
