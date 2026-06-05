package com.jobalert.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import kotlinx.coroutines.launch

/** 알림 설정 — 아침/저녁 푸시 on/off를 백엔드 기기 설정에 반영. */
class NotifSettingsViewModel : ViewModel() {
    private val repository = JobRepository()

    fun setPush(morning: Boolean, evening: Boolean) {
        viewModelScope.launch { runCatching { repository.updatePushPreferences(morning, evening) } }
    }
}
