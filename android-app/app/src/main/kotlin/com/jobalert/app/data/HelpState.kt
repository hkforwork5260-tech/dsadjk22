package com.jobalert.app.data

import android.content.Context

/**
 * 화면별 '처음 진입 도움말'을 한 번만 보여주기 위한 표시 여부 저장(로컬).
 * key 예: "today" / "calendar" / "favorites" / "discover".
 */
object HelpState {
    private const val PREFS = "jobalert_prefs"

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun shown(c: Context, key: String): Boolean = prefs(c).getBoolean("help_$key", false)

    fun markShown(c: Context, key: String) {
        prefs(c).edit().putBoolean("help_$key", true).apply()
    }
}
