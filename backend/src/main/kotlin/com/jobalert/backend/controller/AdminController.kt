package com.jobalert.backend.controller

import com.jobalert.backend.dto.NotificationDto
import com.jobalert.backend.service.HybridCollectorService
import com.jobalert.backend.service.NotificationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    /**
     * 수집을 백그라운드로 시작하고 즉시 202 반환.
     * 수집은 수백 건+본문을 받느라 수십 초~수 분 걸려 클라우드 프록시가 동기 응답에 502를 내므로,
     * HTTP는 바로 끊고 작업은 별도 스레드에서 진행한다. 진행/결과는 서버 로그로 확인.
     * 이미 수집 중이면 409.
     */
    @PostMapping("/collect")
    fun collect(@RequestParam(required = false) source: String?): ResponseEntity<Map<String, Any>> {
        if (collectorService.isRunning) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(mapOf("status" to "already_running", "message" to "수집이 이미 진행 중입니다."))
        }
        // ?source=seoul 처럼 단일 소스만 수집 가능(쉼표로 여러 개). 미지정이면 전체.
        // 가벼운 단일 소스 재수집으로 트라이얼 박스 OOM을 피할 때 쓴다.
        val filter = source?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet()?.takeIf { it.isNotEmpty() }
        collectorService.runDailyCollectionAsync(filter)
        return ResponseEntity.accepted()
            .body(mapOf("status" to "started", "source" to (filter?.joinToString(",") ?: "all"),
                "message" to "수집을 시작했습니다. 진행 상황은 서버 로그를 확인하세요."))
    }

    /**
     * 다이제스트 1건 생성(테스트용). kind=morning_digest(기본)/evening_digest.
     * 실제 발송(FCM)은 별개 — 이건 히스토리 레코드만 생성.
     */
    @PostMapping("/digest")
    fun digest(
        @RequestHeader("X-Device-Id") deviceId: String,
        @RequestParam(defaultValue = "morning_digest") kind: String,
    ): NotificationDto = notificationService.generateDigest(UUID.fromString(deviceId), kind)

    /** 매일 스케줄과 동일하게 등록된 전체 기기에 다이제스트 발송(테스트용). 발송 대상 기기 수 반환. */
    @PostMapping("/digest-all")
    fun digestAll(@RequestParam(defaultValue = "morning_digest") kind: String): Map<String, Any> =
        mapOf("kind" to kind, "sentToDevices" to notificationService.sendDailyDigestToAll(kind))
}

