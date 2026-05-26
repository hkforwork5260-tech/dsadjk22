package com.jobalert.backend.exception

import org.springframework.http.HttpStatus

abstract class JobAlertException(
    val httpStatus: HttpStatus,
    val errorCode: String,
    override val message: String,
) : RuntimeException(message)

class NotFoundException(code: String, message: String) :
    JobAlertException(HttpStatus.NOT_FOUND, code, message)

class BadRequestException(code: String, message: String) :
    JobAlertException(HttpStatus.BAD_REQUEST, code, message)
