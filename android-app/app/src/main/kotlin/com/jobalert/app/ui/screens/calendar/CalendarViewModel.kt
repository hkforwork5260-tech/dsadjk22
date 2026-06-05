package com.jobalert.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.UpcomingResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 마감 캘린더 상태. 진입 시 백엔드 /jobs/upcoming(40일)을 불러 날짜별 마감 공고를 노출.
 */
class CalendarViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<CalendarUiState>(CalendarUiState.Loading)
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = CalendarUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                CalendarUiState.Success(repository.upcoming(40))
            } catch (e: Exception) {
                CalendarUiState.Error(e.message ?: "마감 정보를 불러오지 못했어요")
            }
        }
    }
}

sealed interface CalendarUiState {
    data object Loading : CalendarUiState
    data class Success(val data: UpcomingResponse) : CalendarUiState
    data class Error(val message: String) : CalendarUiState
}
