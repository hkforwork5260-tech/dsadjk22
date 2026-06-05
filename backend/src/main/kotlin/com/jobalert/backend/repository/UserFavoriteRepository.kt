package com.jobalert.backend.repository

import com.jobalert.backend.entity.UserFavorite
import com.jobalert.backend.entity.UserFavoriteId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserFavoriteRepository : JpaRepository<UserFavorite, UserFavoriteId> {
    fun findAllByDeviceId(deviceId: UUID): List<UserFavorite>
    fun existsByDeviceIdAndCompanyId(deviceId: UUID, companyId: Long): Boolean
    fun deleteByDeviceIdAndCompanyId(deviceId: UUID, companyId: Long)
}
