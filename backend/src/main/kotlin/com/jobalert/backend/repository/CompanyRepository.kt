package com.jobalert.backend.repository

import com.jobalert.backend.entity.Company
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyRepository : JpaRepository<Company, Long> {
    fun findByNameNormalized(nameNormalized: String): Company?
    fun existsByNameNormalized(nameNormalized: String): Boolean
    fun findAllByIsApprovedTrue(): List<Company>
}
