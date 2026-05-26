package com.jobalert.backend.service

import com.jobalert.backend.dto.CategoriesResponse
import com.jobalert.backend.dto.CompanyListResponse
import org.springframework.stereotype.Service

@Service
class OnboardingService(
    private val mock: MockDataProvider,
) {
    fun categories(): CategoriesResponse =
        CategoriesResponse(categories = mock.categories)

    fun popularCompanies(categories: List<String>): CompanyListResponse =
        CompanyListResponse(companies = mock.companies)
}
