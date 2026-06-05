package com.jobalert.backend.controller

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyPageResponse
import com.jobalert.backend.service.CompanyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/companies")
class CompanyController(
    private val companyService: CompanyService,
) {

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): CompanyDetailDto = companyService.detail(id)

    /** 회사 상세 페이지 (안드로이드 CompanyDetailScreen용 조립 응답). */
    @GetMapping("/{id}/page")
    fun page(@PathVariable id: Long): CompanyPageResponse = companyService.page(id)

    @GetMapping("/{id}/jobs")
    fun jobs(
        @PathVariable id: Long,
        @RequestParam(required = false) kind: String?,
        @RequestParam(defaultValue = "30") limit: Int,
    ): CompanyJobsResponse = companyService.jobs(id, kind, limit)
}
