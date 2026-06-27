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

    // 가벼운 소스(greenhouse, seoul, samsung)는 06·18시에 순차 수집.
    @Scheduled(cron = "\${jobalert.collector.cron-light}", zone = "UTC")
    fun runLightSources() {
        log.info("수집 트리거 — 가벼운 소스(greenhouse, seoul, samsung) 순차")
        collectorService.runDailyCollectionPerSourceAsync(setOf("greenhouse", "seoul", "samsung"))
    }

    // 무거운 공공기관(상세 500호출)은 30분 뒤 단독 수집 — 다른 소스 메모리 누적 없는 깨끗한 상태에서.
    @Scheduled(cron = "\${jobalert.collector.cron-heavy}", zone = "UTC")
    fun runHeavySource() {
        log.info("수집 트리거 — 공공기관(public-institution) 단독")
        collectorService.runDailyCollectionAsync(setOf("public-institution"))
    }
}
