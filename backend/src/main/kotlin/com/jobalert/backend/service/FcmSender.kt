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
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * FCM 푸시 발송기. 서비스 계정 키가 있고 enabled일 때만 초기화.
 *
 * 키 없거나 비활성이면 [isEnabled]=false, [sendToToken]은 no-op(null). → 키 준비 전에도 앱은 정상 동작.
 * 활성화: FCM_ENABLED=true + 아래 둘 중 하나로 키 제공.
 *  - 클라우드(Railway 등): FCM_CREDENTIALS_JSON 에 서비스계정 JSON 통째를 넣음(파일 못 올리는 환경용).
 *  - 로컬: FCM_CREDENTIALS_PATH(기본 secrets/fcm-service-account.json) 파일 경로.
 * 둘 다 있으면 JSON 우선.
 */
@Component
class FcmSender(
    @Value("\${jobalert.fcm.enabled:false}") private val enabled: Boolean,
    @Value("\${jobalert.fcm.credentials-path:secrets/fcm-service-account.json}") private val credentialsPath: String,
    @Value("\${jobalert.fcm.credentials-json:}") private val credentialsJson: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val app: FirebaseApp? = initApp()

    val isEnabled: Boolean get() = app != null

    private fun initApp(): FirebaseApp? {
        if (!enabled) {
            log.info("FCM 비활성(jobalert.fcm.enabled=false). 푸시 발송 생략.")
            return null
        }
        val credentialStream: InputStream = when {
            credentialsJson.isNotBlank() -> {
                log.info("FCM 키 소스: 환경변수 JSON(FCM_CREDENTIALS_JSON)")
                ByteArrayInputStream(credentialsJson.toByteArray(Charsets.UTF_8))
            }
            File(credentialsPath).exists() -> {
                log.info("FCM 키 소스: 파일({})", credentialsPath)
                File(credentialsPath).inputStream()
            }
            else -> {
                log.warn("FCM 키 없음(FCM_CREDENTIALS_JSON 비었고 파일 {} 없음) — 푸시 발송 생략.", credentialsPath)
                return null
            }
        }
        return try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialStream))
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
