package com.jobalert.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.jobalert.app.MainActivity
import com.jobalert.app.R

/**
 * 홈 위젯 — 새 공고 수 + 꽁이(상황별 표정). 미리보기 없이 단순.
 * 데이터는 [WidgetState](앱이 갱신)에서 읽고, 꽁이는 [MascotRenderer]로 Bitmap 렌더.
 */
class JobAlertWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateWidget(context, manager, it) }
    }

    companion object {
        /** 앱이 데이터(새 공고 수·방문)를 갱신한 뒤 위젯을 즉시 새로고침할 때 호출. */
        fun updateAll(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, JobAlertWidgetProvider::class.java),
            )
            ids.forEach { updateWidget(context, manager, it) }
        }

        private fun updateWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_jobalert)
            val count = WidgetState.newCount(context)
            val expression = WidgetState.expression(context)

            views.setImageViewBitmap(R.id.widget_mascot, MascotRenderer.render(expression, 160))
            views.setTextViewText(R.id.widget_count, if (count > 0) "새 공고 $count" else "새 공고 없음")

            // 위젯 탭 → 앱 열기
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pending = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )
            views.setOnClickPendingIntent(R.id.widget_root, pending)

            manager.updateAppWidget(id, views)
        }
    }
}
