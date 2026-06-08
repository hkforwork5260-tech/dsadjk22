package com.jobalert.app.ui.screens.filter

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jobalert.app.data.fcm.FcmRegistrar

/**
 * 현재 적용된 필터(직군·경력·규모)를 보관하는 전역 상태 홀더 + 로컬 영속.
 *
 * 필터 화면·온보딩에서 고른 조건을 메인 피드로 전달하는 통로. Compose State라 메인이 읽으면
 * 자동 구독 → 변경 시 재조회. SharedPreferences 영속(앱 재시작 유지).
 * MainActivity.onCreate에서 [init] 먼저 호출.
 */
object ActiveFilter {
    private const val PREFS = "jobalert_prefs"
    private const val KEY_CAT = "filter_categories"
    private const val KEY_EXP = "filter_experiences"
    private const val KEY_SIZE = "filter_sizes"
    private const val KEY_DDAY = "filter_deadline_days"
    private const val KEY_ONBOARDED = "onboarding_done"

    private var appContext: Context? = null

    /** 온보딩 완료 여부(처음 설치 때 1회만 보이게). 시작 화면 결정에 사용. */
    var onboardingDone: Boolean = false
        private set

    /** 직군 코드들. 빈 리스트면 전체. */
    var categories by mutableStateOf<List<String>>(emptyList()); private set

    /** 경력 버킷(신입/경력/인턴). 빈 리스트면 전체. */
    var experiences by mutableStateOf<List<String>>(emptyList()); private set

    /** 회사 규모 코드(large_corp/public/…). 빈 리스트면 전체. */
    var sizes by mutableStateOf<List<String>>(emptyList()); private set

    /** 마감일 필터: N일 이내 마감만(0=오늘·3=D-3…). -1이면 전체(필터 없음). */
    var deadlineDays by mutableStateOf(-1); private set

    fun init(context: Context) {
        appContext = context.applicationContext
        categories = load(KEY_CAT)
        experiences = load(KEY_EXP)
        sizes = load(KEY_SIZE)
        deadlineDays = prefs()?.getInt(KEY_DDAY, -1) ?: -1
        onboardingDone = prefs()?.getBoolean(KEY_ONBOARDED, false) ?: false
    }

    /** 온보딩 1회 완료 표시(영속). 이후 앱 시작 시 메인으로 바로 진입. */
    fun markOnboardingDone() {
        onboardingDone = true
        prefs()?.edit()?.putBoolean(KEY_ONBOARDED, true)?.apply()
    }

    /** 필터 설정 + 영속 + 백엔드 재동기화(직군 변경 시 개인화 다이제스트 반영). 미지정 인자는 현재값 유지. */
    fun set(
        categories: List<String> = this.categories,
        experiences: List<String> = this.experiences,
        sizes: List<String> = this.sizes,
        deadlineDays: Int = this.deadlineDays,
    ) {
        this.categories = categories
        this.experiences = experiences
        this.sizes = sizes
        this.deadlineDays = deadlineDays
        save(KEY_CAT, categories)
        save(KEY_EXP, experiences)
        save(KEY_SIZE, sizes)
        prefs()?.edit()?.putInt(KEY_DDAY, deadlineDays)?.apply()
        FcmRegistrar.refresh(categories)
    }

    private fun load(key: String): List<String> =
        prefs()?.getString(key, null)?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    private fun save(key: String, values: List<String>) {
        prefs()?.edit()?.putString(key, values.joinToString(","))?.apply()
    }

    private fun prefs() = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
