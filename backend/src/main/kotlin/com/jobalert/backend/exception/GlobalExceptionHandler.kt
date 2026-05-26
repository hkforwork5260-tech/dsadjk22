package com.jobalert.backend.exception

import com.jobalert.backend.dto.ApiError
import com.jobalert.backend.dto.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(JobAlertException::class)
    fun handleJobAlert(ex: JobAlertException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(ex.httpStatus)
            .body(ErrorResponse(ApiError(ex.errorCode, ex.message)))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(ApiError("VALIDATION_FAILED", "요청 값이 유효하지 않습니다.", details)))
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", ex)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(ApiError("INTERNAL_ERROR", "서버 내부 오류")))
    }
}
