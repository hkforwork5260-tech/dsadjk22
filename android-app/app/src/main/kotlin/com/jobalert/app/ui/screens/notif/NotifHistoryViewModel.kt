package com.jobalert.app.ui.screens.notif

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.api.NotificationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 알림 히스토리 상태. 현재 기기(X-Device-Id) 기준 다이제스트 기록을 백엔드에서 불러온다.
 */
class NotifHistoryViewModel : ViewModel() {

    private val repository = JobRepository()

    private val _state = MutableStateFlow<NotifUiState>(NotifUiState.Loading)
    val state: StateFlow<NotifUiState> = _state.asStateFlow()

    fun load() {
        _state.value = NotifUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                NotifUiState.Success(repository.notifications().notifications)
            } catch (e: Exception) {
                NotifUiState.Error(e.message ?: "알림을 불러오지 못했어요")
            }
        }
    }

    /** 읽음 처리(백엔드 동기화, best-effort). */
    fun markRead(id: String) {
        viewModelScope.launch { runCatching { repository.markNotificationRead(id) } }
    }
}

sealed interface NotifUiState {
    data object Loading : NotifUiState
    data class Success(val notifications: List<NotificationDto>) : NotifUiState
    data class Error(val message: String) : NotifUiState
}
