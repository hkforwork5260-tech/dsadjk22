package com.jobalert.backend.service

import com.jobalert.backend.dto.JobDetailDto
import com.jobalert.backend.dto.JobDto
import com.jobalert.backend.dto.JobKindCounts
import com.jobalert.backend.dto.JobListResponse
import com.jobalert.backend.dto.JobSearchResponse
import com.jobalert.backend.dto.JobUpcomingResponse
import com.jobalert.backend.dto.JobsTodayResponse
import com.jobalert.backend.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.ZoneId

@Service
class JobService(
    private val mock: MockDataProvider,
) {

    fun today(kind: String?, categories: List<String>, limit: Int): JobsTodayResponse {
        val filtered = mock.jobs.filter { kind == null || it.kind == kind }
        val counts = JobKindCounts(
            new = mock.jobs.count { it.kind == "NEW" },
            update = mock.jobs.count { it.kind == "UPDATE" },
            closing = mock.jobs.count { it.kind == "CLOSING" },
        )
        val today = LocalDate.now(ZoneId.of("Asia/Seoul")).toString()
        return JobsTodayResponse(
            date = today,
            counts = counts,
            jobs = filtered.take(limit),
            nextCursor = null,
        )
    }

    fun detail(id: String): JobDetailDto =
        mock.jobDetails[id] ?: throw NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")

    fun similar(id: String): JobListResponse {
        mock.jobDetails[id] ?: throw NotFoundException("JOB_NOT_FOUND", "공고를 찾을 수 없습니다.")
        val base = mock.jobs.first { it.id == id }
        val similar = mock.jobs
            .filter { it.id != id && it.company.industry == base.company.industry }
            .take(10)
        return JobListResponse(jobs = similar)
    }

    fun search(q: String, kind: String?, limit: Int): JobSearchResponse {
        val hits = mock.jobs.filter { j ->
            (kind == null || j.kind == kind) &&
                (j.title.contains(q, ignoreCase = true) ||
                    j.company.name.contains(q, ignoreCase = true) ||
                    j.tags.any { it.contains(q, ignoreCase = true) })
        }
        return JobSearchResponse(
            query = q,
            totalEstimate = hits.size,
            jobs = hits.take(limit),
            nextCursor = null,
        )
    }

    fun upcoming(days: Int): JobUpcomingResponse {
        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val byDate = mock.jobs
            .filter { it.deadline != null }
            .groupBy { it.deadline!!.atZoneSameInstant(ZoneId.of("Asia/Seoul")).toLocalDate().toString() }
            .filterKeys { LocalDate.parse(it).let { d -> !d.isBefore(today) && !d.isAfter(today.plusDays(days.toLong())) } }
            .toSortedMap()
        return JobUpcomingResponse(days = days, byDate = byDate)
    }
}
