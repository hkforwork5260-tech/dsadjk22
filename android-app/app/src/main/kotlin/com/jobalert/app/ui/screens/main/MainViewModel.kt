package com.jobalert.app.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 메인 피드 상태 보유. 화면 진입 시 백엔드 /jobs/today를 불러 [MainUiState]로 노출한다.
 *
 * 동기 mock(SampleJobs)에서 실 네트워크로 전환하며 도입한 첫 ViewModel.
 * 다른 화면도 이 패턴(Repository 주입 + viewModelScope + UiState)을 복제하면 된다.
 */
class MainViewModel @JvmOverloads constructor(
    // @JvmOverloads로 무인자 생성자도 생성 → viewModel() 기본 팩토리가 찾을 수 있음.
    // 인자형은 테스트에서 가짜 Repository 주입용.
    private val repository: JobRepository = JobRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val state: StateFlow<MainUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        _state.value = MainUiState.Loading
        viewModelScope.launch {
            _state.value = try {
                MainUiState.Success(repository.todayFeed())
            } catch (e: Exception) {
                MainUiState.Error(e.message ?: "공고를 불러오지 못했어요")
            }
        }
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val feed: JobRepository.TodayFeed) : MainUiState
    data class Error(val message: String) : MainUiState
}
