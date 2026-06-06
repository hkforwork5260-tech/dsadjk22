package com.jobalert.backend.repository

import com.jobalert.backend.entity.SavedJob
import com.jobalert.backend.entity.SavedJobId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SavedJobRepository : JpaRepository<SavedJob, SavedJobId> {
    fun findAllByDeviceIdOrderByCreatedAtDesc(deviceId: UUID): List<SavedJob>
    fun existsByDeviceIdAndJobId(deviceId: UUID, jobId: String): Boolean
    fun deleteByDeviceIdAndJobId(deviceId: UUID, jobId: String)
}
