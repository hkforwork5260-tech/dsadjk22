package com.jobalert.app.ui.screens.seen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.SeenJobs
import com.jobalert.app.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 본 공고 목록 상태. 직접 '자세히 보기'로 연 공고(로컬 [SeenJobs]) ID들로 백엔드에서 공고를 불러온다.
 * 화면 진입마다 [load].
 */
class SeenJobsViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<SeenJobsUiState>(SeenJobsUiState.Loading)
    val state: StateFlow<SeenJobsUiState> = _state.asStateFlow()

    fun load() {
        _state.value = SeenJobsUiState.Loading
        viewModelScope.launch {
            // SeenJobs는 본 순서를 보존(LinkedHashSet) → 뒤집어 '최근 본' 순으로.
            val ids = SeenJobs.seenIds.toList().reversed()
            _state.value = try {
                SeenJobsUiState.Success(repository.jobsByIds(ids))
            } catch (e: Exception) {
                SeenJobsUiState.Error(e.message ?: "본 공고를 불러오지 못했어요")
            }
        }
    }
}

sealed interface SeenJobsUiState {
    data object Loading : SeenJobsUiState
    data class Success(val jobs: List<Job>) : SeenJobsUiState
    data class Error(val message: String) : SeenJobsUiState
}
