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
    private const val KEY_CLOSING_COUNT = "widget_closing_count"
    private const val KEY_TOP_JOB = "widget_top_job"
    private const val KEY_LAST_VISIT = "widget_last_visit"
    private const val DAY_MS = 24L * 60 * 60 * 1000

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 앱이 위젯에 보여줄 요약 갱신: 새 공고 수 + 마감임박 수 + 대표 공고 1개("회사 · 직무"). */
    fun setSummary(context: Context, newCount: Int, closingCount: Int, topJob: String) {
        prefs(context).edit()
            .putInt(KEY_NEW_COUNT, newCount)
            .putInt(KEY_CLOSING_COUNT, closingCount)
            .putString(KEY_TOP_JOB, topJob)
            .apply()
    }

    /** 앱 진입 시 방문 기록(표정 판단용). */
    fun markVisited(context: Context) {
        prefs(context).edit().putLong(KEY_LAST_VISIT, System.currentTimeMillis()).apply()
    }

    fun newCount(context: Context): Int = prefs(context).getInt(KEY_NEW_COUNT, 0)
    fun closingCount(context: Context): Int = prefs(context).getInt(KEY_CLOSING_COUNT, 0)
    fun topJob(context: Context): String = prefs(context).getString(KEY_TOP_JOB, "").orEmpty()

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
