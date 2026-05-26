package com.jobalert.backend.repository

import com.jobalert.backend.entity.CompanyAlias
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompanyAliasRepository : JpaRepository<CompanyAlias, Long> {
    fun findByAliasNormalized(aliasNormalized: String): CompanyAlias?
    fun findAllByCompanyId(companyId: Long): List<CompanyAlias>
}
