package com.jobalert.backend.scheduler

import com.jobalert.backend.service.JobCollectorService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 매일 18:00 KST = 09:00 UTC. cron은 서버 UTC 기준.
 * 활성화 조건: jobalert.collector.enabled=true (기본 false — Phase 3에서 켜기)
 */
@Component
@ConditionalOnProperty(name = ["jobalert.collector.enabled"], havingValue = "true")
class CollectorScheduler(
    private val collectorService: JobCollectorService,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "\${jobalert.collector.cron}", zone = "UTC")
    fun runDaily() {
        log.info("CollectorScheduler.runDaily 트리거")
        collectorService.runDailyCollection()
    }
}
