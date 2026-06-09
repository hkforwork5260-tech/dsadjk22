package com.jobalert.app.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 오늘 피드 영속 캐시 — 무료 박스가 cold start/OOM으로 죽어도 앱이 빈 화면을 띄우지 않게.
 *
 * 마지막으로 성공한 today 응답을 SharedPreferences에 통째 저장(JSON)해두고, 다음 진입·앱 재시작 시
 * **즉시** 보여준 뒤(서버 안 기다림) 백그라운드로 네트워크 갱신한다(stale-while-revalidate).
 * 필터별로 나누지 않고 "마지막 성공 피드 1개"만 저장 — 빈 화면 방지가 목적이라 그걸로 충분.
 *
 * [ActiveFilter]·[SeenJobs]와 같은 prefs("jobalert_prefs") + appContext 패턴. MainActivity에서 [init] 호출.
 */
object FeedCache {
    private const val PREFS = "jobalert_prefs"
    private const val KEY_TODAY = "today_feed_cache_v1"

    private var appContext: Context? = null
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun prefs() = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 성공한 today 피드를 저장(실패해도 앱 흐름 방해 안 함). */
    fun saveToday(feed: JobRepository.TodayFeed) {
        runCatching {
            prefs()?.edit()?.putString(KEY_TODAY, json.encodeToString(feed))?.apply()
        }
    }

    /** 저장된 today 피드(없거나 파싱 실패 시 null). 직렬화 스키마가 바뀌면 조용히 무시되고 네트워크로 폴백. */
    fun loadToday(): JobRepository.TodayFeed? =
        prefs()?.getString(KEY_TODAY, null)?.let { s ->
            runCatching { json.decodeFromString<JobRepository.TodayFeed>(s) }.getOrNull()
        }
}
