package com.jobalert.app.ui.screens.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 찾아보기(Reels) — 오늘 피드 공고를 한 장씩 넘겨본다. */
class DiscoverViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    /** 현재 기기의 관심기업 회사 ID 집합. 하트 표시·토글에 사용. */
    private val _favoriteCompanyIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteCompanyIds: StateFlow<Set<Int>> = _favoriteCompanyIds.asStateFlow()

    fun load() {
        viewModelScope.launch {
            runCatching { repository.todayFeed().jobs }.onSuccess { _jobs.value = it }
        }
        viewModelScope.launch {
            runCatching { repository.favorites().companies.map { it.company.id }.toSet() }
                .onSuccess { _favoriteCompanyIds.value = it }
        }
    }

    /** 공고의 회사를 관심기업 추가/삭제. 낙관적 토글 후 실패 시 롤백. */
    fun toggleFavorite(companyId: Int) {
        val willAdd = companyId !in _favoriteCompanyIds.value
        _favoriteCompanyIds.value =
            if (willAdd) _favoriteCompanyIds.value + companyId else _favoriteCompanyIds.value - companyId
        viewModelScope.launch {
            val ok = runCatching {
                if (willAdd) repository.addFavorite(companyId) else repository.removeFavorite(companyId)
            }.isSuccess
            if (!ok) {
                _favoriteCompanyIds.value =
                    if (willAdd) _favoriteCompanyIds.value - companyId else _favoriteCompanyIds.value + companyId
            }
        }
    }
}
