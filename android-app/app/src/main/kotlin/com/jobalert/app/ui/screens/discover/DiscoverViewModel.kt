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

    /** 현재 기기의 저장(북마크) 공고 ID 집합. 마이페이지 저장공고와 동일 서버 소스. */
    private val _savedJobIds = MutableStateFlow<Set<String>>(emptySet())
    val savedJobIds: StateFlow<Set<String>> = _savedJobIds.asStateFlow()

    fun load() {
        viewModelScope.launch {
            runCatching { repository.todayFeed().jobs }.onSuccess { _jobs.value = it }
        }
        viewModelScope.launch {
            runCatching { repository.favorites().companies.map { it.company.id }.toSet() }
                .onSuccess { _favoriteCompanyIds.value = it }
        }
        viewModelScope.launch {
            runCatching { repository.savedJobs().map { it.id }.toSet() }
                .onSuccess { _savedJobIds.value = it }
        }
    }

    /** 공고 저장/해제(서버 연동). 낙관적 토글 후 실패 시 롤백. */
    fun toggleSave(jobId: String) {
        val willAdd = jobId !in _savedJobIds.value
        _savedJobIds.value =
            if (willAdd) _savedJobIds.value + jobId else _savedJobIds.value - jobId
        viewModelScope.launch {
            val ok = if (willAdd) repository.saveJob(jobId) else repository.unsaveJob(jobId)
            if (!ok) {
                _savedJobIds.value =
                    if (willAdd) _savedJobIds.value - jobId else _savedJobIds.value + jobId
            }
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
