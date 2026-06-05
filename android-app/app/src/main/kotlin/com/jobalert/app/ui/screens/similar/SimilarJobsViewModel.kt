package com.jobalert.app.ui.screens.similar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.model.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** 비슷한 공고 상태. 현재 공고(/jobs/{id}) + 같은 업종 추천(/jobs/{id}/similar)을 불러온다. */
class SimilarJobsViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<SimilarUiState>(SimilarUiState.Loading)
    val state: StateFlow<SimilarUiState> = _state.asStateFlow()

    private var lastId: String? = null

    fun load(id: String) {
        if (id == lastId) return
        lastId = id
        _state.value = SimilarUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                val current = runCatching { repository.jobDetail(id) }.getOrNull()
                SimilarUiState.Success(current = current, others = repository.similar(id))
            } catch (e: Exception) {
                SimilarUiState.Error(e.message ?: "비슷한 공고를 불러오지 못했어요")
            }
        }
    }
}

sealed interface SimilarUiState {
    data object Loading : SimilarUiState
    data class Success(val current: Job?, val others: List<Job>) : SimilarUiState
    data class Error(val message: String) : SimilarUiState
}
