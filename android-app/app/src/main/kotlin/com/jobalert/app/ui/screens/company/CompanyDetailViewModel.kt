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

    /**
     * 관심기업 추가/삭제. UI가 낙관적으로 먼저 토글한 뒤 호출하고,
     * [onResult]로 성공/실패를 돌려준다(실패 시 화면이 토글을 롤백·안내).
     */
    fun setFavorite(companyId: Int, favorite: Boolean, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = runCatching {
                if (favorite) repository.addFavorite(companyId) else repository.removeFavorite(companyId)
            }.isSuccess
            onResult(ok)
        }
    }
}

sealed interface CompanyUiState {
    data object Loading : CompanyUiState
    data class Success(val data: CompanyDetailResponse) : CompanyUiState
    data class Error(val message: String) : CompanyUiState
}
