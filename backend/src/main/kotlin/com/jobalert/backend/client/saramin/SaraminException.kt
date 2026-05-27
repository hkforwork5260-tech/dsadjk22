package com.jobalert.backend.client.saramin

/**
 * 사람인 OpenAPI 호출 실패 시 던지는 예외.
 *
 * 사람인 자체 에러 코드:
 *  - 1: access-key 미입력
 *  - 2: 유효하지 않은 access-key
 *  - 3: 유효하지 않은 파라미터
 *  - 4: 일일 최대 요청 초과
 *  - 99: 기타 오류
 */
class SaraminApiException(
    /** 사람인 응답 code (있을 때만). null이면 HTTP 레벨 에러 또는 IO 에러. */
    val saraminCode: Int?,
    /** HTTP status code (4xx/5xx) 또는 200(사람인 application-level 에러). */
    val httpStatus: Int,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    val isRateLimited: Boolean get() = saraminCode == 4
    val isAuthError: Boolean get() = saraminCode == 1 || saraminCode == 2
    val isBadParam: Boolean get() = saraminCode == 3
    val isRetryable: Boolean get() = httpStatus in 500..599 && saraminCode == null
}
