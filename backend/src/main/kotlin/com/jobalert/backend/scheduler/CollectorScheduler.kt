package com.jobalert.backend.scheduler

import com.jobalert.backend.service.HybridCollectorService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 매일 18:00 KST = 09:00 UTC. cron은 서버 UTC 기준.
 * 활성화 조건: jobalert.collector.enabled=true (기본 false — Phase 3에서 켜기)
 *
 * 하이브리드 전환(2026-06-04): 사람인 전용 JobCollectorService → 다중 소스 HybridCollectorService.
 */
@Component
@ConditionalOnProperty(name = ["jobalert.collector.enabled"], havingValue = "true")
class CollectorScheduler(
    private val collectorService: HybridCollectorService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${jobalert.collector.cron}", zone = "UTC")
    fun runDaily() {
        log.info("CollectorScheduler.runDaily 트리거 — per-source 순차 수집")
        // 전체를 한 번에 받으면 무료 박스 OOM → 소스별 순차(메모리 피크 분산)로 수집.
        collectorService.runDailyCollectionPerSourceAsync()
    }
}
