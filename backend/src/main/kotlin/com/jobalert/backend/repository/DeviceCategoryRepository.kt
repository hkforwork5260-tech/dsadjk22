package com.jobalert.backend.repository

import com.jobalert.backend.entity.DeviceCategory
import com.jobalert.backend.entity.DeviceCategoryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DeviceCategoryRepository : JpaRepository<DeviceCategory, DeviceCategoryId> {
    fun findAllByDeviceId(deviceId: UUID): List<DeviceCategory>

    /** 기기의 기존 관심직군 일괄 삭제(즉시 실행 — 재등록 전 정리). */
    @Modifying
    @Query("DELETE FROM DeviceCategory d WHERE d.deviceId = :deviceId")
    fun deleteByDeviceId(@Param("deviceId") deviceId: UUID)
}
