package com.jobalert.app.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jobalert.app.data.FeedCache
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

    // 마지막 성공 응답의 필터 키 + 시각. 같은 필터로 TTL 이내 재진입이면 재요청을 생략(즉시 표시).
    private data class FeedKey(
        val cats: List<String>, val exps: List<String>, val szs: List<String>, val deadlineDays: Int,
    )
    private var cachedKey: FeedKey? = null
    private var cachedAt: Long = 0L

    /**
     * 화면이 LaunchedEffect(필터)·ON_RESUME으로 호출. 같은 필터 & 이미 Success & TTL 이내면 재요청 생략
     * → 탭 왕복·잠깐 복귀 시 네트워크 0(즉시). 필터가 바뀌면 키가 달라져 자동 재조회(캐시 무효화).
     * [force]=true(명시적 '다시 시도')면 캐시 무시.
     */
    fun load(
        categories: List<String> = emptyList(),
        experiences: List<String> = emptyList(),
        sizes: List<String> = emptyList(),
        deadlineDays: Int = -1,
        force: Boolean = false,
    ) {
        val key = FeedKey(categories, experiences, sizes, deadlineDays)
        val fresh = System.currentTimeMillis() - cachedAt < TTL_MS
        if (!force && key == cachedKey && _state.value is MainUiState.Success && fresh) return

        // 화면이 아직 비어있으면(첫 진입·앱 재시작) 영속 캐시를 '즉시' 표시 → 서버를 안 기다림(무료 박스가
        // cold/OOM이어도 빈 화면 X). 그 뒤 아래에서 백그라운드로 최신 갱신(stale-while-revalidate).
        if (_state.value !is MainUiState.Success) {
            val cached = FeedCache.loadToday()
            _state.value = if (cached != null) MainUiState.Success(cached) else MainUiState.Loading
        }

        viewModelScope.launch {
            // 무료 박스 cold start(쉬다 깨어나는 첫 요청)로 타임아웃/502 나면 자동 재시도 — 박스가
            // 깨어나면 다음 시도에서 성공한다. (이래서 '처음 한 번만 안 되던' 문제)
            var lastError: Exception? = null
            repeat(5) { attempt ->
                try {
                    val feed = repository.todayFeed(categories, experiences, sizes, deadlineDays)
                    _state.value = MainUiState.Success(feed)
                    cachedKey = key
                    cachedAt = System.currentTimeMillis()
                    FeedCache.saveToday(feed)   // 다음 cold start 때 즉시 보여줄 수 있게 영속 저장
                    return@launch
                } catch (e: Exception) {
                    lastError = e
                    if (attempt < 4) kotlinx.coroutines.delay((attempt + 1) * 2000L)  // 2·4·6·8초 점증
                }
            }
            // 네트워크 다 실패: 이미 캐시로 Success 표시 중이면 그대로 유지(에러 안 띄움). 캐시도 없으면 Error.
            if (_state.value !is MainUiState.Success) {
                _state.value = MainUiState.Error(lastError?.message ?: "공고를 불러오지 못했어요")
            }
        }
    }

    companion object {
        private const val TTL_MS = 5 * 60 * 1000L  // 5분: 그 이내 재진입은 캐시, 지나면 ON_RESUME이 재조회(마감 반영)
    }
}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val feed: JobRepository.TodayFeed) : MainUiState
    data class Error(val message: String) : MainUiState
}
