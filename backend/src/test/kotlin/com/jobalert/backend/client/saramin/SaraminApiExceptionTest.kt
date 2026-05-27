package com.jobalert.backend.client.saramin

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SaraminApiExceptionTest {

    @Test
    fun `code 4는 rate limited`() {
        val ex = SaraminApiException(saraminCode = 4, httpStatus = 200, message = "한도 초과")
        assertTrue(ex.isRateLimited)
        assertFalse(ex.isAuthError)
        assertFalse(ex.isRetryable)
    }

    @Test
    fun `code 1·2는 auth error`() {
        assertTrue(SaraminApiException(saraminCode = 1, httpStatus = 200, message = "x").isAuthError)
        assertTrue(SaraminApiException(saraminCode = 2, httpStatus = 200, message = "x").isAuthError)
        assertFalse(SaraminApiException(saraminCode = 3, httpStatus = 200, message = "x").isAuthError)
    }

    @Test
    fun `5xx + 사람인코드 없음이면 retryable`() {
        val ex = SaraminApiException(saraminCode = null, httpStatus = 503, message = "down")
        assertTrue(ex.isRetryable)
    }

    @Test
    fun `5xx여도 사람인코드 있으면 retryable 아님 (application-level 에러)`() {
        val ex = SaraminApiException(saraminCode = 99, httpStatus = 500, message = "?")
        assertFalse(ex.isRetryable)
    }

    @Test
    fun `4xx는 retryable 아님`() {
        val ex = SaraminApiException(saraminCode = null, httpStatus = 400, message = "bad")
        assertFalse(ex.isRetryable)
    }
}
