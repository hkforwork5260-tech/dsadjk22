package com.jobalert.app.ui.screens.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 저장한 공고 목록 상태. 현재 기기(X-Device-Id) 기준 저장 공고를 백엔드에서 불러온다.
 * 화면이 진입할 때마다 [load]를 호출해 최신 상태로 갱신.
 */
class SavedJobsViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<SavedJobsUiState>(SavedJobsUiState.Loading)
    val state: StateFlow<SavedJobsUiState> = _state.asStateFlow()

    fun load() {
        _state.value = SavedJobsUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                SavedJobsUiState.Success(repository.savedJobs())
            } catch (e: Exception) {
                SavedJobsUiState.Error(e.message ?: "저장한 공고를 불러오지 못했어요")
            }
        }
    }
}

sealed interface SavedJobsUiState {
    data object Loading : SavedJobsUiState
    data class Success(val jobs: List<Job>) : SavedJobsUiState
    data class Error(val message: String) : SavedJobsUiState
}
