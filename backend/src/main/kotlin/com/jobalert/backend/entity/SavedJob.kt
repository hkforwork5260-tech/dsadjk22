package com.jobalert.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.io.Serializable
import java.time.OffsetDateTime
import java.util.UUID

/**
 * 저장한 공고(북마크) — 기기(device) ↔ 공고(job) M:N. 복합 PK (device_id, job_id).
 * [UserFavorite](회사용)와 대칭. 로그인 없이 익명 기기ID 기준으로 저장한다.
 */
@Entity
@Table(name = "saved_jobs")
@IdClass(SavedJobId::class)
class SavedJob(
    @Id
    @Column(name = "device_id")
    var deviceId: UUID = UUID.randomUUID(),

    @Id
    @Column(name = "job_id")
    var jobId: String = "",

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
)

/** 복합 키 클래스. 필드명·타입이 [SavedJob]의 @Id 필드와 일치해야 한다. */
data class SavedJobId(
    var deviceId: UUID = UUID.randomUUID(),
    var jobId: String = "",
) : Serializable
