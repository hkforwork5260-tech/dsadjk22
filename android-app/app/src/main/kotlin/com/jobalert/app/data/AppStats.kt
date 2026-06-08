package com.jobalert.app.data

import android.content.Context

/**
 * 앱 사용 통계(설치 기준). '단이가 N일째 챙겨주는 중' 카운트에 사용.
 *
 * 설치 시각은 PackageManager.firstInstallTime(실제 APK 설치 시각)을 직접 읽는다.
 * 별도 저장이 필요 없고, prefs를 지워도 정확하다.
 */
object AppStats {

    /** 설치한 날부터 며칠째인지(설치 당일 = 1일째). 실패 시 1. */
    fun daysSinceInstall(context: Context): Int = try {
        val first = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .firstInstallTime
        val days = (System.currentTimeMillis() - first) / 86_400_000L
        (days + 1).toInt().coerceAtLeast(1)
    } catch (e: Exception) {
        1
    }
}
