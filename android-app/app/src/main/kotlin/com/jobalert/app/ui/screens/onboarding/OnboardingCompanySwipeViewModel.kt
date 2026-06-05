package com.jobalert.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.CompanyDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 온보딩 추천 회사 — 공고 많은 회사 순(실데이터). */
class OnboardingCompanySwipeViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _companies = MutableStateFlow<List<CompanyDto>>(emptyList())
    val companies: StateFlow<List<CompanyDto>> = _companies.asStateFlow()

    fun load() {
        viewModelScope.launch {
            runCatching { repository.popularCompanies().companies }.onSuccess { _companies.value = it }
        }
    }
}
