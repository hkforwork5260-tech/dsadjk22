package com.jobalert.app.data.api

import android.content.Context
import java.util.UUID

/**
 * 익명 기기 식별자. 로그인 없이 관심기업·알림설정을 이 기기 기준으로 저장하기 위한 ID.
 *
 * 앱 첫 실행 시 UUID를 생성해 SharedPreferences에 영속(앱 삭제 전까지 유지).
 * [ApiClient] 인터셉터가 모든 요청에 `X-Device-Id` 헤더로 실어 보낸다.
 * MainActivity.onCreate에서 [init]을 먼저 호출해야 한다.
 */
object DeviceId {
    private const val PREFS = "jobalert_prefs"
    private const val KEY = "device_id"

    @Volatile
    private var cached: String? = null

    fun init(context: Context) {
        if (cached != null) return
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var id = prefs.getString(KEY, null)
        if (id == null) {
            id = UUID.randomUUID().toString()
            prefs.edit().putString(KEY, id).apply()
        }
        cached = id
    }

    /** 현재 기기ID. init 전 호출 시 고정 fallback(요청은 가되 빈 결과). */
    val value: String
        get() = cached ?: "00000000-0000-0000-0000-000000000000"
}
