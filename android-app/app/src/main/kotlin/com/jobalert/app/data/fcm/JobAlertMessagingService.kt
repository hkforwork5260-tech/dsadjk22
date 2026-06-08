package com.jobalert.app.data.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jobalert.app.ui.screens.filter.ActiveFilter

/**
 * FCM 수신 서비스.
 *
 * - [onNewToken]: 토큰 발급/갱신 시 백엔드에 재등록.
 * - 백엔드가 'notification' 타입으로 보낸 다이제스트는 앱이 백그라운드일 때 **시스템이 자동으로**
 *   잠금화면에 표시한다(여기 코드 불필요). 포그라운드 수신 시의 커스텀 표시는 추후.
 */
class JobAlertMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // 저장된 관심직군을 함께 보냄(빈 값 보내면 백엔드 직군이 지워지므로 prefs 로드 후 등록).
        ActiveFilter.init(applicationContext)
        FcmRegistrar.register(token, ActiveFilter.interestCategories, ActiveFilter.interestSizes)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // 포그라운드 수신. v0.1은 시스템 표시(백그라운드)에 의존 — 별도 처리 없음.
    }
}
