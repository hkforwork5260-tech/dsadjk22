package com.jobalert.app.ui.screens.filter

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 현재 적용된 직군 필터를 보관하는 전역 상태 홀더 + 로컬 영속.
 *
 * 필터 화면·온보딩 직군 선택에서 고른 직군을 메인 피드로 전달하는 통로.
 * Compose State라 메인 화면이 [categories]를 읽으면 자동 구독 → 변경 시 재조회된다.
 * SharedPreferences에 저장해 앱 재시작에도 유지(온보딩은 1회만 뜨므로 필수).
 * MainActivity.onCreate에서 [init]을 먼저 호출.
 */
object ActiveFilter {
    private const val PREFS = "jobalert_prefs"
    private const val KEY = "filter_categories"

    private var appContext: Context? = null

    /** 적용된 직군 코드들. 빈 리스트면 전체. */
    var categories by mutableStateOf<List<String>>(emptyList())
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
        val saved = prefs()?.getString(KEY, null)
        categories = saved?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    /** 직군 필터 설정 + 영속. */
    fun set(codes: List<String>) {
        categories = codes
        prefs()?.edit()?.putString(KEY, codes.joinToString(","))?.apply()
    }

    private fun prefs() = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
