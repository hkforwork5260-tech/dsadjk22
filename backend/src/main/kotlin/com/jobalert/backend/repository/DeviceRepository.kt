package com.jobalert.backend.repository

import com.jobalert.backend.entity.Device
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeviceRepository : JpaRepository<Device, UUID> {
    fun findByFcmToken(fcmToken: String): Device?

    /** 푸시 대상 — FCM 토큰이 등록된 기기 전체. */
    fun findAllByFcmTokenIsNotNull(): List<Device>
}
