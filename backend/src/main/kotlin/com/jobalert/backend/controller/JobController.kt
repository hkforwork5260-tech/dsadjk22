package com.jobalert.backend.controller

import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobListResponse
import com.jobalert.backend.dto.JobSearchResponse
import com.jobalert.backend.dto.JobUpcomingResponse
import com.jobalert.backend.dto.JobsTodayResponse
import com.jobalert.backend.service.JobService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

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
    ): JobsTodayResponse = jobService.today(kind, categories, experiences, sizes, limit)

    @GetMapping("/upcoming")
    fun upcoming(@RequestParam(defaultValue = "14") days: Int): JobUpcomingResponse =
        jobService.upcoming(days)

    @GetMapping("/search")
    fun search(
        @RequestParam q: String,
        @RequestParam(required = false) kind: String?,
        @RequestParam(defaultValue = "20") limit: Int,
    ): JobSearchResponse = jobService.search(q, kind, limit)

    @GetMapping("/{id}")
    fun detail(@PathVariable id: String): JobDetailDto = jobService.detail(id)

    @GetMapping("/{id}/similar")
    fun similar(@PathVariable id: String): JobListResponse = jobService.similar(id)
}
