package com.jobalert.app.widget

import android.content.Context
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * 위젯이 스스로 백엔드(today)를 호출해 '관심 기준 오늘 새 공고 수 + 마감임박 수'를 갱신한다.
 * 앱을 열지 않아도 위젯 주기(약 30분)마다 최신 수를 보여주기 위함 — 사용자가 위젯만 보고도
 * "오늘 새 공고 N" 을 확인하고 앱으로 들어오게 한다.
 *
 * 관심(직군·규모)·기기ID는 앱이 저장한 prefs("jobalert_prefs")에서 그대로 읽어 today 필터로 보낸다.
 */
object WidgetUpdater {
    private const val BASE = "https://dsadjk22-production.up.railway.app/api/v1/jobs/today"

    fun refreshFromServer(context: Context) {
        Thread {
            try {
                val prefs = context.getSharedPreferences("jobalert_prefs", Context.MODE_PRIVATE)
                val cats = prefs.getString("filter_categories", "").orEmpty()   // 관심 직군(콤마)
                val sizes = prefs.getString("filter_sizes", "").orEmpty()        // 관심 규모(콤마)
                val deviceId = prefs.getString("device_id", null)
                val sb = StringBuilder("$BASE?limit=1")
                if (cats.isNotBlank()) sb.append("&categories=").append(cats)
                if (sizes.isNotBlank()) sb.append("&sizes=").append(sizes)
                val conn = (URL(sb.toString()).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 6000
                    readTimeout = 8000
                    deviceId?.let { setRequestProperty("X-Device-Id", it) }
                }
                if (conn.responseCode == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val counts = JSONObject(body).optJSONObject("counts")
                    val newC = counts?.optInt("new", 0) ?: 0
                    val closing = counts?.optInt("closing", 0) ?: 0
                    val topJob = prefs.getString("widget_top_job", "").orEmpty()
                    WidgetState.setSummary(context, newC, closing, topJob)
                    JobAlertWidgetProvider.updateAll(context)
                }
                conn.disconnect()
            } catch (e: Exception) {
                // 무시 — 다음 주기 재시도(오프라인/서버 일시 다운 등).
            }
        }.start()
    }
}
