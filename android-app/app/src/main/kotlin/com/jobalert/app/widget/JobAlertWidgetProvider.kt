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

    // 사용자가 위젯 크기를 바꾸면(리사이즈) 레이아웃을 다시 골라 갱신.
    override fun onAppWidgetOptionsChanged(
        context: Context,
        manager: AppWidgetManager,
        id: Int,
        newOptions: android.os.Bundle?,
    ) {
        updateWidget(context, manager, id)
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
            // 위젯 크기(dp)에 맞는 레이아웃 선택: 작으면 tiny(꽁이+숫자), 높이 1줄이면 wide(가로), 그 외 세로.
            val opts = manager.getAppWidgetOptions(id)
            val minW = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 0)
            val minH = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 0)
            val layout = when {
                minH in 1 until 110 && minW in 1 until 110 -> R.layout.widget_jobalert_tiny
                minH in 1 until 110 -> R.layout.widget_jobalert_wide
                else -> R.layout.widget_jobalert
            }
            val tiny = layout == R.layout.widget_jobalert_tiny
            val wide = layout == R.layout.widget_jobalert_wide

            val views = RemoteViews(context.packageName, layout)
            val count = WidgetState.newCount(context)
            val expression = WidgetState.expression(context)

            views.setImageViewBitmap(R.id.widget_mascot, MascotRenderer.render(context, expression, 160))
            when {
                tiny -> {
                    views.setTextViewText(R.id.widget_count, if (count > 0) "$count" else "0")
                    views.setTextViewText(R.id.widget_label, "채용알리미")
                }
                wide -> {
                    // 2x1: "채용알리미" 자리에 새 공고 수 숫자를 크게. 위는 라벨.
                    views.setTextViewText(R.id.widget_count, "새 공고")
                    views.setTextViewText(R.id.widget_label, "$count")
                }
                else -> {
                    views.setTextViewText(R.id.widget_count, if (count > 0) "새 공고 $count" else "새 공고 없음")
                    views.setTextViewText(R.id.widget_label, "채용알리미")
                }
            }

            // 큰(세로) 레이아웃에만 마감임박·대표공고 추가(작은/가로엔 해당 뷰 없음).
            if (layout == R.layout.widget_jobalert) {
                val closing = WidgetState.closingCount(context)
                views.setTextViewText(
                    R.id.widget_closing,
                    if (closing > 0) "⏰ 마감 임박 $closing" else "",
                )
            }

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
