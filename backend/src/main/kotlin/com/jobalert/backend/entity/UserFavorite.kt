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
 * 관심기업 — 기기(device) ↔ 회사(company) M:N. 복합 PK (device_id, company_id).
 * 로그인 없이 익명 기기ID 기준으로 즐겨찾기를 저장한다.
 */
@Entity
@Table(name = "user_favorites")
@IdClass(UserFavoriteId::class)
class UserFavorite(
    @Id
    @Column(name = "device_id")
    var deviceId: UUID = UUID.randomUUID(),

    @Id
    @Column(name = "company_id")
    var companyId: Long = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),
)

/** 복합 키 클래스. 필드명·타입이 [UserFavorite]의 @Id 필드와 일치해야 한다. */
data class UserFavoriteId(
    var deviceId: UUID = UUID.randomUUID(),
    var companyId: Long = 0,
) : Serializable
