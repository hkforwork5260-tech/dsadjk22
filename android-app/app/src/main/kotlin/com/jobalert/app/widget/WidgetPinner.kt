package com.jobalert.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.os.Build

/** 위젯 핀 추가 크기. 각 크기는 자기 widget_info XML(targetCell)을 가진 별도 provider. */
enum class WidgetPinSize { SMALL, MEDIUM, LARGE }

/**
 * JobAlert 위젯을 홈 화면에 "고정 추가" 요청한다(선택한 크기로).
 *
 * Android 8(API 26)+ 이고 런처가 핀 고정을 지원하면, 시스템이 "추가할까요?" 팝업을 띄워
 * 사용자가 한 번 탭하면 바로 추가된다. 핀 크기는 OS가 widget XML(targetCell)로만 정하므로
 * 크기별로 별도 provider([JobAlertWidgetSmall]/[Medium]/[Large])를 지정해 호출한다.
 *
 * @return 팝업 요청에 성공했으면 true. 미지원 런처/구버전이면 false(이땐 수동 추가 안내).
 */
fun Context.requestPinJobAlertWidget(size: WidgetPinSize = WidgetPinSize.MEDIUM): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false
    val manager = AppWidgetManager.getInstance(this)
    if (!manager.isRequestPinAppWidgetSupported) return false
    val cls = when (size) {
        WidgetPinSize.SMALL -> JobAlertWidgetSmall::class.java
        WidgetPinSize.MEDIUM -> JobAlertWidgetMedium::class.java
        WidgetPinSize.LARGE -> JobAlertWidgetLarge::class.java
    }
    return manager.requestPinAppWidget(ComponentName(this, cls), null, null)
}
