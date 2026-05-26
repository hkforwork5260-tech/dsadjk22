package com.jobalert.backend.dto

data class ApiError(
    val code: String,
    val message: String,
    val details: Map<String, Any?>? = null,
)

data class ErrorResponse(
    val error: ApiError,
)
