package com.jobalert.backend.controller

import com.jobalert.backend.dto.CategoriesResponse
import com.jobalert.backend.dto.CompanyListResponse
import com.jobalert.backend.service.OnboardingService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/onboarding")
class OnboardingController(
    private val onboardingService: OnboardingService,
) {

    @GetMapping("/categories")
    fun categories(): CategoriesResponse = onboardingService.categories()

    @GetMapping("/popular-companies")
    fun popularCompanies(
        @RequestParam(required = false, defaultValue = "") categories: List<String>,
    ): CompanyListResponse = onboardingService.popularCompanies(categories)
}
