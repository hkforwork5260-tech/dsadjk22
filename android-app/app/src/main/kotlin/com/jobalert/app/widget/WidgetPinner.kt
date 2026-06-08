package com.jobalert.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build

/**
 * JobAlert 위젯을 홈 화면에 "고정 추가" 요청한다.
 *
 * Android 8(API 26)+ 이고 런처가 핀 고정을 지원하면, 시스템이 "홈 화면에 위젯을 추가할까요?"
 * 팝업을 띄워 사용자가 한 번 탭하면 바로 추가된다. (앱이 위젯을 몰래 배치하는 건 OS가 막으므로,
 * 이 시스템 팝업이 "한 번 탭 추가"의 최선이다.)
 *
 * @return 팝업 요청에 성공했으면 true. 미지원 런처/구버전이면 false(이땐 수동 추가 안내).
 */
fun Context.requestPinJobAlertWidget(): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    val manager = AppWidgetManager.getInstance(this)
    if (!manager.isRequestPinAppWidgetSupported) return false
    val provider = ComponentName(this, JobAlertWidgetProvider::class.java)
    return manager.requestPinAppWidget(provider, null, null)
}
