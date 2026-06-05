package com.jobalert.backend.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File

/**
 * FCM 푸시 발송기. 서비스 계정 키(jobalert.fcm.credentials-path)가 있고 enabled일 때만 초기화.
 *
 * 키 없거나 비활성이면 [isEnabled]=false, [sendToToken]은 no-op(null). → 키 준비 전에도 앱은 정상 동작.
 * 활성화: FCM_ENABLED=true + FCM_CREDENTIALS_PATH(기본 secrets/fcm-service-account.json).
 */
@Component
class FcmSender(
    @Value("\${jobalert.fcm.enabled:false}") private val enabled: Boolean,
    @Value("\${jobalert.fcm.credentials-path:secrets/fcm-service-account.json}") private val credentialsPath: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val app: FirebaseApp? = initApp()

    val isEnabled: Boolean get() = app != null

    private fun initApp(): FirebaseApp? {
        if (!enabled) {
            log.info("FCM 비활성(jobalert.fcm.enabled=false). 푸시 발송 생략.")
            return null
        }
        val file = File(credentialsPath)
        if (!file.exists()) {
            log.warn("FCM 키 없음: {} — 푸시 발송 생략.", credentialsPath)
            return null
        }
        return try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(file.inputStream()))
                .build()
            val app = if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            } else {
                FirebaseApp.getInstance()
            }
            log.info("FCM 초기화 완료 (project={})", app.options.projectId)
            app
        } catch (ex: Exception) {
            log.error("FCM 초기화 실패 — 푸시 발송 생략", ex)
            null
        }
    }

    /** 단일 기기 토큰으로 알림 발송. 성공 시 messageId, 실패/비활성 시 null. */
    fun sendToToken(token: String, title: String, body: String): String? {
        val app = app ?: return null
        if (token.isBlank()) return null
        val message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build()
        return try {
            FirebaseMessaging.getInstance(app).send(message)
        } catch (ex: Exception) {
            log.warn("FCM 발송 실패 token={}…: {}", token.take(12), ex.message)
            null
        }
    }
}
