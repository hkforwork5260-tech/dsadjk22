package com.jobalert.backend.seed

import com.jobalert.backend.entity.Company
import com.jobalert.backend.repository.CompanyRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

/**
 * companies 테이블이 비어 있으면 부팅 시 seed CSV 적재.
 * v0.1 placeholder ~57개. Phase 3에서 공정위 공시 데이터 1000개로 확장.
 */
@Configuration
@ConditionalOnProperty(name = ["jobalert.seed.enabled"], havingValue = "true", matchIfMissing = true)
class CompanySeedLoader {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun seedCompanies(repository: CompanyRepository): ApplicationRunner = ApplicationRunner {
        if (repository.count() > 0) {
            log.info("companies 테이블에 이미 {} 행 존재. seed 스킵.", repository.count())
            return@ApplicationRunner
        }
        val resource = ClassPathResource("seed/companies.csv")
        if (!resource.exists()) {
            log.warn("seed/companies.csv 미존재. seed 스킵.")
            return@ApplicationRunner
        }
        val rows = resource.inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.drop(1).filter { it.isNotBlank() }.map { line ->
                val cols = line.split(",").map { it.trim() }
                Company(
                    name = cols.getOrNull(0).orEmpty(),
                    nameNormalized = cols.getOrNull(1).orEmpty(),
                    industry = cols.getOrNull(2)?.takeIf { it.isNotBlank() },
                    groupName = cols.getOrNull(3)?.takeIf { it.isNotBlank() },
                    size = cols.getOrNull(4)?.takeIf { it.isNotBlank() },
                    domain = cols.getOrNull(5)?.takeIf { it.isNotBlank() },
                    logoUrl = cols.getOrNull(5)?.takeIf { it.isNotBlank() }?.let { "https://logo.clearbit.com/$it" },
                    isApproved = true,
                )
            }.toList()
        }
        repository.saveAll(rows)
        log.info("회사 시드 {} 개 적재 완료.", rows.size)
    }
}
