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
}

sealed interface JobDetailUiState {
    data object Loading : JobDetailUiState
    data class Success(val job: Job) : JobDetailUiState
    data class Error(val message: String) : JobDetailUiState
}
