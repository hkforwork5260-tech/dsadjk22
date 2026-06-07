package com.jobalert.backend.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * `/api/v1/admin/` 하위 경로를 토큰으로 보호한다(v0.1 경량 인증).
 *
 * spring-security를 쓰면 전체 엔드포인트가 기본 잠금이라 공개 조회 API까지 막힌다.
 * 관리자 트리거만 보호하면 되므로, 해당 경로만 매핑한 [HandlerInterceptor]로 가볍게 처리한다.
 *
 * 동작:
 *  - `jobalert.admin.token`(ADMIN_TOKEN)이 설정돼 있으면 `X-Admin-Token` 헤더가 일치해야 통과. 아니면 401.
 *  - 토큰이 빈값(미설정)이면 무인증 개방 — 로컬 개발 편의. 시작 시 경고 로그로 알린다.
 *
 * 일일 자동수집 cron은 서버 내부 호출이라 이 인터셉터를 타지 않는다(HTTP 아님).
 */
@Component
class AdminAuthInterceptor(
    @Value("\${jobalert.admin.token:}") private val token: String,
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    init {
        if (token.isBlank()) {
            log.warn("⚠️ ADMIN_TOKEN 미설정 — /api/v1/admin/* 가 무인증 개방 상태입니다. 프로덕션은 ADMIN_TOKEN 설정 필수.")
        }
    }

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (token.isBlank()) return true // 미설정 = 개발용 개방

        val provided = request.getHeader("X-Admin-Token")
        if (provided == token) return true

        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write("""{"error":"unauthorized","message":"X-Admin-Token 헤더가 필요합니다."}""")
        return false
    }
}
