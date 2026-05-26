package com.jobalert.backend.service

import com.jobalert.backend.entity.ApiCallLog
import com.jobalert.backend.repository.ApiCallLogRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ApiCallLogger(
    private val repo: ApiCallLogRepository,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun log(
        source: String,
        endpoint: String? = null,
        params: Map<String, Any?>? = null,
        statusCode: Int? = null,
        durationMs: Int? = null,
        errorMessage: String? = null,
    ) {
        try {
            repo.save(
                ApiCallLog(
                    source = source,
                    endpoint = endpoint,
                    requestParams = params,
                    statusCode = statusCode,
                    durationMs = durationMs,
                    errorMessage = errorMessage,
                    calledAt = OffsetDateTime.now(),
                )
            )
        } catch (ex: DataAccessException) {
            log.warn("api_call_log 저장 실패 (DB 미준비?): {}", ex.message)
        }
    }

    fun countSaraminCallsLast24h(): Long =
        repo.countSince("saramin", OffsetDateTime.now().minusHours(24))
}
