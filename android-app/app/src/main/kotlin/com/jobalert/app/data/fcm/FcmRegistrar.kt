package com.jobalert.app.data.fcm

import com.jobalert.app.data.api.ApiClient
import com.jobalert.app.data.api.DeviceId
import com.jobalert.app.data.api.DeviceRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FCM 토큰을 백엔드에 등록 — 이 기기를 푸시 발송 대상으로 만든다.
 * 앱 시작 시(현재 토큰) + 토큰 갱신 시([JobAlertMessagingService.onNewToken]) 호출.
 */
object FcmRegistrar {
    fun register(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                ApiClient.api.registerDevice(
                    DeviceRegisterRequest(fcmToken = token, platform = "android", deviceId = DeviceId.value),
                )
            }
        }
    }
}
