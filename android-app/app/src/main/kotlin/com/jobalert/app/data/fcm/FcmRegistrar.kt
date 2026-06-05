package com.jobalert.app.data.fcm

import com.google.firebase.messaging.FirebaseMessaging
import com.jobalert.app.data.api.ApiClient
import com.jobalert.app.data.api.DeviceId
import com.jobalert.app.data.api.DevicePreferences
import com.jobalert.app.data.api.DeviceRegisterRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FCM 토큰 + 관심직군을 백엔드에 등록 — 이 기기를 (개인화) 푸시 발송 대상으로 만든다.
 * 앱 시작 시 + 토큰 갱신 시 + 관심직군 변경 시([refresh]) 호출.
 */
object FcmRegistrar {
    /** 현재 FCM 토큰을 받아 관심직군과 함께 재등록. 직군이 바뀌었을 때 호출. */
    fun refresh(categories: List<String>) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            register(token, categories)
        }
    }

    fun register(token: String, categories: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                ApiClient.api.registerDevice(
                    DeviceRegisterRequest(
                        fcmToken = token,
                        platform = "android",
                        deviceId = DeviceId.value,
                        preferences = DevicePreferences(categories = categories),
                    ),
                )
            }
        }
    }
}
