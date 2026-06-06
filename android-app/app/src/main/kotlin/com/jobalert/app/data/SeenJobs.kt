package com.jobalert.app.data

import android.content.Context

/**
 * 찾아보기에서 이미 본 공고 ID 기록(로컬). 다음 진입 시 본 공고를 피드 뒤로 보내,
 * 매번 처음부터 같은 공고를 다시 보지 않게 한다.
 *
 * SharedPreferences에 영속. MainActivity.onCreate에서 [init] 먼저 호출.
 */
object SeenJobs {
    private const val PREFS = "jobalert_prefs"
    private const val KEY = "seen_job_ids"

    private val cache = mutableSetOf<String>()
    private var prefs: android.content.SharedPreferences? = null

    fun init(context: Context) {
        if (prefs != null) return
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        prefs = p
        cache.addAll(p.getStringSet(KEY, emptySet()).orEmpty())
    }

    /** 지금까지 본 공고 ID 스냅샷. */
    val seenIds: Set<String> get() = cache.toSet()

    /** 공고를 본 것으로 기록(영속). */
    fun markSeen(id: String) {
        if (cache.add(id)) {
            prefs?.edit()?.putStringSet(KEY, cache.toSet())?.apply()
        }
    }
}
