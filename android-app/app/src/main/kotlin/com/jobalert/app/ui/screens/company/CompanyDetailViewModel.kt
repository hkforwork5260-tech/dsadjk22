package com.jobalert.app.ui.screens.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.CompanyDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 회사 상세 상태. 화면 진입 시 companyId로 백엔드 /companies/{id}/page 호출.
 */
class CompanyDetailViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<CompanyUiState>(CompanyUiState.Loading)
    val state: StateFlow<CompanyUiState> = _state.asStateFlow()

    private var lastId: Int? = null

    fun load(companyId: Int) {
        if (companyId == lastId) return
        lastId = companyId
        _state.value = CompanyUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                CompanyUiState.Success(repository.companyDetail(companyId))
            } catch (e: Exception) {
                CompanyUiState.Error(e.message ?: "회사 정보를 불러오지 못했어요")
            }
        }
    }

    /** 관심기업 추가/삭제. UI는 낙관적으로 먼저 토글되고, 실패해도 best-effort. */
    fun setFavorite(companyId: Int, favorite: Boolean) {
        viewModelScope.launch {
            runCatching {
                if (favorite) repository.addFavorite(companyId) else repository.removeFavorite(companyId)
            }
        }
    }
}

sealed interface CompanyUiState {
    data object Loading : CompanyUiState
    data class Success(val data: CompanyDetailResponse) : CompanyUiState
    data class Error(val message: String) : CompanyUiState
}
