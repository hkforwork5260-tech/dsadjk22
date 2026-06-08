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

    /** 첫 로드 완료 여부. 로딩 중엔 '오늘은 여기까지' finish 카드를 띄우지 않기 위함. */
    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    fun load() {
        viewModelScope.launch {
            runCatching { repository.discoverFeed() }
                // 탐색 피드는 매번 순서를 바꿔 다양하게(인스타 탐색 느낌). 백엔드도 랜덤 셔플하지만
                // 점수 가중치로 상위가 고정처럼 보일 수 있어, 받은 목록을 클라에서 한 번 더 셔플한다.
                // (본 공고 후순위는 화면에서 SeenJobs로 다시 정렬)
                .onSuccess { _jobs.value = it.shuffled() }
            _isLoaded.value = true
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
