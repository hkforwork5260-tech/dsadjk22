package com.jobalert.app.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.FavoriteCompanyDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 관심기업 목록 상태. 현재 기기(X-Device-Id) 기준 즐겨찾기를 백엔드에서 불러온다.
 * 화면이 진입할 때마다 [load]를 호출해 최신 상태로 갱신(추가/삭제 반영).
 */
class FavoritesViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val state: StateFlow<FavoritesUiState> = _state.asStateFlow()

    fun load() {
        _state.value = FavoritesUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                FavoritesUiState.Success(repository.favorites().companies)
            } catch (e: Exception) {
                FavoritesUiState.Error(e.message ?: "관심 기업을 불러오지 못했어요")
            }
        }
    }
}

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val companies: List<FavoriteCompanyDto>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}
