package com.jobalert.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.jobalert.app.data.SeenJobs
import com.jobalert.app.data.api.ApiClient
import com.jobalert.app.data.api.DeviceId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.jobalert.app.data.fcm.FcmRegistrar
import com.jobalert.app.widget.WidgetState
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.nav.JobAlertNavHost
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.JobAlertTheme

class MainActivity : ComponentActivity() {

    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* 허용 여부 무관, best-effort */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DeviceId.init(applicationContext)      // 관심기업 등 기기ID 헤더 준비
        ActiveFilter.init(applicationContext)  // 저장된 관심 직군 필터 로드
        SeenJobs.init(applicationContext)      // 찾아보기 본 공고 기록(후순위 정렬)
        com.jobalert.app.data.FeedCache.init(applicationContext)  // 오늘 피드 영속 캐시(서버 다운 시 즉시 표시)
        WidgetState.markVisited(applicationContext)  // 위젯 단이 표정용 방문 기록
        warmUpServer()   // 무료 박스가 잠들어 있을 수 있어, 온보딩/첫 화면 보는 동안 미리 깨운다.
        requestNotificationPermission()
        registerFcmToken()
        enableEdgeToEdge()
        setContent {
            JobAlertTheme {
                androidx.compose.foundation.layout.Box(
                    Modifier
                        .fillMaxSize()
                        .background(HiFiColors.Bg)
                        .windowInsetsPadding(WindowInsets.systemBars),
                ) {
                    JobAlertNavHost()
                }
            }
        }
    }

    /** 안드로이드 13+ 알림 권한 런타임 요청 (없으면 푸시가 안 보임). */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            if (!granted) notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    /**
     * 무료 박스(Railway) cold start 대비: 앱 시작 시 가벼운 요청을 던져 서버를 미리 깨운다.
     * 깨어날 때까지(502/실패) 잠깐 간격으로 몇 번 재시도. 사용자가 온보딩 하는 동안 워밍업되어
     * 첫 '오늘' 로드가 바로 성공한다. (fire-and-forget, 실패해도 무시)
     */
    private fun warmUpServer() {
        CoroutineScope(Dispatchers.IO).launch {
            repeat(6) {
                val ok = runCatching { ApiClient.api.jobsToday(limit = 1) }.isSuccess
                if (ok) return@launch
                delay(3000)
            }
        }
    }

    /** 현재 FCM 토큰 + 관심직군을 백엔드에 등록(이 기기를 개인화 푸시 대상으로). */
    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            FcmRegistrar.register(token, ActiveFilter.interestCategories, ActiveFilter.interestSizes)
        }
    }
}
