package com.jobalert.backend.controller

import com.jobalert.backend.dto.CompanyDetailDto
import com.jobalert.backend.dto.CompanyDto
import com.jobalert.backend.dto.CompanyJobsResponse
import com.jobalert.backend.dto.CompanyPageResponse
import com.jobalert.backend.service.CompanyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/companies")
class CompanyController(
    private val companyService: CompanyService,
) {

    /** 회사명 검색 — 관심기업 추가 화면. 진행중 공고 있는 회사만, 활성 공고수 많은 순. */
    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestHeader("X-Device-Id", required = false) deviceId: String?,
    ): List<CompanyDto> {
        val device = deviceId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        return companyService.search(query, device, limit)
    }

    @GetMapping("/{id}")
    fun detail(@PathVariable id: Long): CompanyDetailDto = companyService.detail(id)

    /** 회사 상세 페이지 (안드로이드 CompanyDetailScreen용 조립 응답). 기기ID 있으면 즐겨찾기 여부 반영. */
    @GetMapping("/{id}/page")
    fun page(
        @PathVariable id: Long,
        @RequestHeader("X-Device-Id", required = false) deviceId: String?,
    ): CompanyPageResponse {
        val device = deviceId?.let { runCatching { UUID.fromString(it) }.getOrNull() }
        return companyService.page(id, device)
    }

    @GetMapping("/{id}/jobs")
    fun jobs(
        @PathVariable id: Long,
        @RequestParam(required = false) kind: String?,
        @RequestParam(defaultValue = "30") limit: Int,
    ): CompanyJobsResponse = companyService.jobs(id, kind, limit)
}
