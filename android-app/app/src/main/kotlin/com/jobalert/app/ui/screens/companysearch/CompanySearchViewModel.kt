package com.jobalert.app.ui.screens.companysearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.CompanyDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 관심기업 추가용 회사 검색. 입력 디바운스(300ms) 후 /companies/search 호출.
 * ★ 토글은 낙관적 업데이트 후 실패 시 롤백(즉각 반응 + 정합성).
 */
class CompanySearchViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow(CompanySearchUiState())
    val state: StateFlow<CompanySearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(q: String) {
        _state.value = _state.value.copy(query = q)
        searchJob?.cancel()
        if (q.isBlank()) {
            _state.value = _state.value.copy(results = emptyList(), loading = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _state.value = _state.value.copy(loading = true)
            val res = runCatching { repository.companySearch(q.trim()) }.getOrDefault(emptyList())
            _state.value = _state.value.copy(results = res, loading = false)
        }
    }

    fun toggleFavorite(company: CompanyDto) {
        val nowFav = !company.isFavorited
        fun setFav(fav: Boolean) {
            _state.value = _state.value.copy(
                results = _state.value.results.map { if (it.id == company.id) it.copy(isFavorited = fav) else it },
            )
        }
        setFav(nowFav)
        viewModelScope.launch {
            runCatching {
                if (nowFav) repository.addFavorite(company.id) else repository.removeFavorite(company.id)
            }.onFailure { setFav(!nowFav) } // 롤백
        }
    }
}

data class CompanySearchUiState(
    val query: String = "",
    val results: List<CompanyDto> = emptyList(),
    val loading: Boolean = false,
)
