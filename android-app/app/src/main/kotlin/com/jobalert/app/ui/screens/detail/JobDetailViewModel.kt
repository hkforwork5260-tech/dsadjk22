package com.jobalert.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 공고 상세 상태. /jobs/{id}에서 실 공고를 불러온다. */
class JobDetailViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<JobDetailUiState>(JobDetailUiState.Loading)
    val state: StateFlow<JobDetailUiState> = _state.asStateFlow()

    private var lastId: String? = null

    fun load(id: String) {
        if (id == lastId) return
        lastId = id
        _state.value = JobDetailUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                JobDetailUiState.Success(repository.jobDetail(id))
            } catch (e: Exception) {
                JobDetailUiState.Error(e.message ?: "공고를 불러오지 못했어요")
            }
        }
    }

    /**
     * 공고 저장/해제. UI가 낙관적으로 먼저 토글한 뒤 호출하고,
     * [onResult]로 성공/실패를 돌려준다(실패 시 화면이 토글을 롤백·안내).
     */
    fun setSaved(jobId: String, saved: Boolean, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = if (saved) repository.saveJob(jobId) else repository.unsaveJob(jobId)
            onResult(ok)
        }
    }

    /** 이 공고 회사를 관심기업 추가/삭제. 낙관적 토글 후 실패는 [onResult]로. */
    fun setFavorite(companyId: Int, favorite: Boolean, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = runCatching {
                if (favorite) repository.addFavorite(companyId) else repository.removeFavorite(companyId)
            }.isSuccess
            onResult(ok)
        }
    }
}

sealed interface JobDetailUiState {
    data object Loading : JobDetailUiState
    data class Success(val job: Job) : JobDetailUiState
    data class Error(val message: String) : JobDetailUiState
}
