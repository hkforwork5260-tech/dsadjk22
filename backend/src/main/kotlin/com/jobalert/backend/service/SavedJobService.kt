package com.jobalert.backend.service

import com.jobalert.backend.dto.SaveToggleResponse
import com.jobalert.backend.dto.SavedJobsResponse
import com.jobalert.backend.entity.Company
import com.jobalert.backend.entity.Device
import com.jobalert.backend.entity.SavedJob
import com.jobalert.backend.exception.NotFoundException
import com.jobalert.backend.repository.CompanyRepository
import com.jobalert.backend.repository.DeviceRepository
import com.jobalert.backend.repository.JobRepository
import com.jobalert.backend.repository.SavedJobRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 저장한 공고(북마크) — 로그인 없이 익명 기기ID 기준으로 saved_jobs 테이블에 저장.
 * [FavoriteService](관심기업)와 대칭. 기기는 저장 시 없으면 자동 생성.
 */
@Service
@Transactional
class SavedJobService(
    private val savedJobRepository: SavedJobRepository,
    private val deviceRepository: DeviceRepository,
    private val jobRepository: JobRepository,
    private val companyRepository: CompanyRepository,
    private val mapper: JobMapper,
) {
    fun add(deviceId: UUID, jobId: String): SaveToggleResponse {
        // 존재하지 않는 공고는 저장 불가(FK 위반 방지 + 명확한 404).
        if (!jobRepository.existsById(jobId)) {
            throw NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        }
        ensureDevice(deviceId)
        if (!savedJobRepository.existsByDeviceIdAndJobId(deviceId, jobId)) {
            savedJobRepository.save(SavedJob(deviceId = deviceId, jobId = jobId))
        }
        return SaveToggleResponse(saved = true, jobId = jobId)
    }

    fun remove(deviceId: UUID, jobId: String): SaveToggleResponse {
        savedJobRepository.deleteByDeviceIdAndJobId(deviceId, jobId)
        return SaveToggleResponse(saved = false, jobId = jobId)
    }

    @Transactional(readOnly = true)
    fun list(deviceId: UUID): SavedJobsResponse {
        val ids = savedJobRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId).map { it.jobId }
        if (ids.isEmpty()) return SavedJobsResponse(emptyList())

        // findAllById는 순서를 보장하지 않으므로 저장 최신순(ids)으로 복원. 삭제된 공고는 스킵.
        val byId = jobRepository.findAllById(ids).associateBy { it.id }
        val ordered = ids.mapNotNull { byId[it] }
        val companies: Map<Long, Company> =
            companyRepository.findAllById(ordered.map { it.companyId }.toSet()).associateBy { it.id!! }
        return SavedJobsResponse(ordered.map { mapper.toDto(it, companies[it.companyId]) })
    }

    /** saved_jobs.device_id 는 devices FK라 기기 행이 먼저 있어야 한다. 없으면 최소 정보로 생성. */
    private fun ensureDevice(deviceId: UUID) {
        if (!deviceRepository.existsById(deviceId)) {
            deviceRepository.save(Device(deviceId = deviceId, platform = "android"))
        }
    }
}
