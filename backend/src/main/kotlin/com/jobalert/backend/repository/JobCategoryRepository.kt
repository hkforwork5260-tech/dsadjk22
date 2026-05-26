package com.jobalert.backend.repository

import com.jobalert.backend.entity.JobCategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JobCategoryRepository : JpaRepository<JobCategoryEntity, String> {
    fun findAllByOrderBySortOrderAsc(): List<JobCategoryEntity>
}
