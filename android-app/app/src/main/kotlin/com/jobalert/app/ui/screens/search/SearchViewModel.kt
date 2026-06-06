package com.jobalert.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.JobsSearchResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 검색 결과 상태. 화면이 검색어를 줄 때마다 [search]로 백엔드 /jobs/search 호출.
 * 메인과 같은 패턴(Repository + viewModelScope + UiState).
 */
class SearchViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var lastKey: String? = null

    /**
     * 검색어(query) 또는 직군(categoryCode)으로 조회. 화면에서 LaunchedEffect로 트리거.
     * categoryCode가 있으면 "직군별 둘러보기", 없으면 키워드 검색.
     */
    fun search(query: String, categoryCode: String? = null) {
        val key = "$query|$categoryCode"
        if (key == lastKey) return
        lastKey = key
        _state.value = SearchUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                val cats = categoryCode?.let { listOf(it) } ?: emptyList()
                SearchUiState.Success(repository.search(query, cats))
            } catch (e: Exception) {
                SearchUiState.Error(e.message ?: "검색에 실패했어요")
            }
        }
    }
}

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Success(val response: JobsSearchResponse) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
