package com.jobalert.app.widget

import android.content.Context
import com.jobalert.app.ui.components.MascotExpression

/**
 * 위젯이 표시할 상태(새 공고 수 + 마지막 방문 시각)와 그에 따른 꽁이 표정 로직.
 * 앱이 값을 갱신(메인 로드 시 새 공고 수, 앱 진입 시 방문 기록)하면 위젯이 읽어 그린다.
 */
object WidgetState {
    private const val PREFS = "jobalert_prefs"
    private const val KEY_NEW_COUNT = "widget_new_count"
    private const val KEY_LAST_VISIT = "widget_last_visit"
    private const val DAY_MS = 24L * 60 * 60 * 1000

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 앱이 "오늘 새 공고(NEW)" 수를 갱신. */
    fun setNewCount(context: Context, count: Int) {
        prefs(context).edit().putInt(KEY_NEW_COUNT, count).apply()
    }

    /** 앱 진입 시 방문 기록(표정 판단용). */
    fun markVisited(context: Context) {
        prefs(context).edit().putLong(KEY_LAST_VISIT, System.currentTimeMillis()).apply()
    }

    fun newCount(context: Context): Int = prefs(context).getInt(KEY_NEW_COUNT, 0)

    /**
     * 상황별 꽁이 표정:
     * - 새 공고 0 → Sleep(자는 꽁이)
     * - 3일+ 미방문 → Sad(삐진 꽁이)  ← "왜 안 봐!"
     * - 새 공고 5개+ → Wow(신난 꽁이)
     * - 그 외(1~4개, 최근 방문) → Happy
     */
    fun expression(context: Context): MascotExpression {
        val count = newCount(context)
        val lastVisit = prefs(context).getLong(KEY_LAST_VISIT, 0L)
        val daysSince = if (lastVisit == 0L) 0L else (System.currentTimeMillis() - lastVisit) / DAY_MS
        return when {
            count == 0 -> MascotExpression.Sleep
            daysSince >= 3 -> MascotExpression.Sad
            count >= 5 -> MascotExpression.Wow
            else -> MascotExpression.Happy
        }
    }
}
