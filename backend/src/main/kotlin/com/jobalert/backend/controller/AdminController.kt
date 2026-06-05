package com.jobalert.backend.controller

import com.jobalert.backend.dto.NotificationDto
import com.jobalert.backend.service.HybridCollectorService
import com.jobalert.backend.service.NotificationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * 운영·테스트용 수동 트리거.
 *
 * 평소 수집은 매일 18시 KST cron([com.jobalert.backend.scheduler.CollectorScheduler])이 돌리지만,
 * 개발 중엔 cron을 기다릴 수 없으니 여기서 즉시 실행한다.
 *
 * ⚠️ v0.1은 인증 없음. 프로덕션 노출 전 관리자 인증/내부망 제한 필요(별도 태스크).
 */
@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val collectorService: HybridCollectorService,
    private val notificationService: NotificationService,
) {
    /** 즉시 1회 수집 + DB 적재. 결과(소스별 건수·적재 통계) 반환. */
    @PostMapping("/collect")
    fun collect(): HybridCollectorService.CollectionResult = collectorService.runDailyCollection()

    /**
     * 다이제스트 1건 생성(테스트용). kind=morning_digest(기본)/evening_digest.
     * 실제 발송(FCM)은 별개 — 이건 히스토리 레코드만 생성.
     */
    @PostMapping("/digest")
    fun digest(
        @RequestHeader("X-Device-Id") deviceId: String,
        @RequestParam(defaultValue = "morning_digest") kind: String,
    ): NotificationDto = notificationService.generateDigest(UUID.fromString(deviceId), kind)
}
