package com.jobalert.backend.controller

import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobListResponse
import com.jobalert.backend.dto.JobSearchResponse
import com.jobalert.backend.dto.JobUpcomingResponse
import com.jobalert.backend.dto.JobsTodayResponse
import com.jobalert.backend.service.JobService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val jobService: JobService,
) {

    @GetMapping("/today")
    fun today(
        @RequestParam(required = false) kind: String?,
        @RequestParam(required = false, defaultValue = "") categories: List<String>,
        @RequestParam(required = false, defaultValue = "") experiences: List<String>,
        @RequestParam(required = false, defaultValue = "") sizes: List<String>,
        @RequestParam(defaultValue = "30") limit: Int,
        @RequestParam(required = false) deadlineDays: Int?,
        @RequestHeader(value = "X-Device-Id", required = false) deviceId: String?,
    ): JobsTodayResponse =
        // 피드는 헤더 없이도 동작해야 하므로, 형식이 틀린 기기ID는 무시하고 비개인화로 폴백한다.
        jobService.today(kind, categories, experiences, sizes, limit, parseDeviceId(deviceId), deadlineDays)

    private fun parseDeviceId(raw: String?): UUID? =
        raw?.takeIf { it.isNotBlank() }?.let { runCatching { UUID.fromString(it) }.getOrNull() }

    @GetMapping("/upcoming")
    fun upcoming(@RequestParam(defaultValue = "14") days: Int): JobUpcomingResponse =
        jobService.upcoming(days)

    @GetMapping("/search")
    fun search(
        @RequestParam(required = false, defaultValue = "") q: String,
        @RequestParam(required = false) kind: String?,
        @RequestParam(required = false, defaultValue = "") categories: List<String>,
        @RequestParam(defaultValue = "50") limit: Int,
    ): JobSearchResponse = jobService.search(q, kind, categories, limit)

    // 리터럴 경로라 /{id}보다 우선 매칭됨. '본 공고' 등 클라가 가진 ID 목록으로 공고 조회.
    @GetMapping("/by-ids")
    fun byIds(@RequestParam(required = false, defaultValue = "") ids: List<String>): JobListResponse =
        jobService.byIds(ids)

    // 찾아보기 전용 피드(인스타 탐색 랭킹). '오늘' 필터와 무관. 개인화 위해 X-Device-Id 사용.
    @GetMapping("/discover")
    fun discover(
        @RequestParam(defaultValue = "200") limit: Int,
        @RequestHeader(value = "X-Device-Id", required = false) deviceId: String?,
    ): JobListResponse = jobService.discover(parseDeviceId(deviceId), limit)

    @GetMapping("/{id}")
    fun detail(
        @PathVariable id: String,
        @RequestHeader(value = "X-Device-Id", required = false) deviceId: String?,
    ): JobDetailDto = jobService.detail(id, parseDeviceId(deviceId))

    @GetMapping("/{id}/similar")
    fun similar(@PathVariable id: String): JobListResponse = jobService.similar(id)
}
