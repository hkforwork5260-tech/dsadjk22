package com.jobalert.backend.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI

/**
 * 회사명·홈페이지URL → Clearbit Logo URL 빌드.
 *
 * Clearbit Logo API 특징:
 *  - 키·인증 불필요. URL 자체가 이미지 endpoint (https://logo.clearbit.com/{domain}).
 *  - 호출 한도 없음 (Logo는 무료). 실제 다운로드는 안드로이드 ImageLoader가.
 *  - 도메인 없으면 404 → 안드로이드는 회사 첫 글자 텍스트 fallback.
 *
 * domain 추정 순서:
 *  1. homepageUrl이 있으면 거기서 host 추출
 *  2. 영문 회사명이면 lowercase + 공백제거 + ".com"
 *  3. 한글 회사명이면 [KNOWN_DOMAINS] 사전 lookup
 *  4. 위 모두 miss → null (logoUrl도 null)
 */
@Component
class CompanyLogoResolver {

    private val log = LoggerFactory.getLogger(javaClass)

    fun resolveLogoUrl(companyName: String?, homepageUrl: String? = null): String? {
        val domain = resolveDomain(companyName, homepageUrl) ?: return null
        return "$CLEARBIT_BASE/$domain"
    }

    fun resolveDomain(companyName: String?, homepageUrl: String? = null): String? {
        // 1) homepageUrl 우선
        homepageUrl?.takeIf { it.isNotBlank() }?.let { url ->
            extractHost(url)?.let { return stripWww(it) }
        }

        if (companyName.isNullOrBlank()) return null

        // 2) 한글 회사명 dictionary
        val trimmed = companyName.trim()
        KNOWN_DOMAINS[trimmed]?.let { return it }
        // 정규화된 키로도 한 번 더 시도 (사람인 표기 변형 흡수)
        val normalized = CompanyNameNormalizer.normalize(trimmed)
        if (normalized.isNotBlank()) {
            KNOWN_DOMAINS_NORMALIZED[normalized]?.let { return it }
        }

        // 3) 영문 회사명 → {name}.com
        if (isAsciiOnly(trimmed)) {
            val slug = trimmed.lowercase()
                .replace(Regex("""\s+"""), "")
                .replace(Regex("""[^a-z0-9-]"""), "")
            if (slug.isNotEmpty()) return "$slug.com"
        }

        log.debug("domain 추정 실패: companyName='{}' homepageUrl='{}'", companyName, homepageUrl)
        return null
    }

    private fun extractHost(url: String): String? = runCatching {
        val withScheme = if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
        URI(withScheme).host
    }.getOrNull()?.takeIf { it.isNotBlank() }

    private fun stripWww(host: String): String =
        if (host.startsWith("www.")) host.removePrefix("www.") else host

    private fun isAsciiOnly(s: String): Boolean = s.all { it.code < 128 }

    companion object {
        const val CLEARBIT_BASE = "https://logo.clearbit.com"

        /**
         * 1000개 회사 시드를 채우기 전 임시 사전.
         * Phase 3에서 seed CSV에 domain 컬럼을 채우면 이 매핑은 fallback 용도로 축소.
         */
        private val KNOWN_DOMAINS = mapOf(
            // 빅테크·플랫폼
            "삼성전자" to "samsung.com",
            "네이버" to "naver.com",
            "카카오" to "kakao.com",
            "쿠팡" to "coupang.com",
            "라인" to "linecorp.com",
            "배달의민족" to "woowahan.com",
            "우아한형제들" to "woowahan.com",
            "토스" to "toss.im",
            "비바리퍼블리카" to "toss.im",
            "당근마켓" to "daangn.com",
            "당근" to "daangn.com",
            // 전통 대기업
            "LG에너지솔루션" to "lgensol.com",
            "LG전자" to "lge.com",
            "LG화학" to "lgchem.com",
            "현대자동차" to "hyundai.com",
            "기아" to "kia.com",
            "포스코" to "posco.com",
            "포스코홀딩스" to "poscoholdings.com",
            "SK하이닉스" to "skhynix.com",
            "SK텔레콤" to "sktelecom.com",
            "KT" to "kt.com",
            "한화" to "hanwha.co.kr",
            "두산" to "doosan.com",
            "아모레퍼시픽" to "apgroup.com",
            // 금융
            "신한은행" to "shinhan.com",
            "KB국민은행" to "kbstar.com",
            "하나은행" to "kebhana.com",
            "우리은행" to "wooribank.com",
            "삼성생명" to "samsunglife.com",
        )

        private val KNOWN_DOMAINS_NORMALIZED: Map<String, String> = KNOWN_DOMAINS
            .entries
            .associate { CompanyNameNormalizer.normalize(it.key) to it.value }
            .filterKeys { it.isNotBlank() }
    }
}
