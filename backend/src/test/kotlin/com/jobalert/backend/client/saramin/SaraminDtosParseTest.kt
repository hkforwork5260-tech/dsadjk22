package com.jobalert.backend.client.saramin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * 사람인 OpenAPI 공식 응답 형식을 Jackson으로 파싱하는지 검증.
 * 공식 명세: https://oapi.saramin.co.kr/guide/job-search
 *
 * 키 받은 뒤 실 응답으로 통합 검증을 한 번 더 해야 하지만,
 * kebab-case 필드 매핑(`@JsonProperty`)과 nested wrapper 깨짐 여부는 여기서 잡힌다.
 */
class SaraminDtosParseTest {

    private val mapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @Test
    fun `사람인 정상 응답 JSON을 파싱한다`() {
        val json = """
        {
          "jobs": {
            "count": 110,
            "start": 0,
            "total": "1234",
            "job": [
              {
                "id": "46123456",
                "url": "https://www.saramin.co.kr/zf_user/jobs/relay/view?rec_idx=46123456",
                "active": "1",
                "company": {
                  "detail": {
                    "name": "삼성전자",
                    "href": "https://www.samsung.com/sec/"
                  }
                },
                "position": {
                  "title": "2026 상반기 신입공채",
                  "industry": {"code": "100", "name": "전기·전자"},
                  "location": {"code": "101000", "name": "서울 강남구"},
                  "job-type": {"code": "1", "name": "정규직"},
                  "job-mid-code": {"code": "84", "name": "IT개발·데이터"},
                  "job-code": {"code": "84100", "name": "백엔드 개발"},
                  "experience-level": {"code": "1", "min": "0", "max": "0", "name": "신입"},
                  "required-education-level": {"code": "8", "name": "대학교(4년)"}
                },
                "keyword": "Java,Kotlin,Spring",
                "salary": {"code": "0", "name": "회사내규에 따름"},
                "posting-timestamp": "1716595200",
                "modification-timestamp": "1716595300",
                "opening-timestamp": "1716595200",
                "expiration-timestamp": "1718064000",
                "close-type": {"code": "1", "name": "마감일까지"}
              }
            ]
          }
        }
        """.trimIndent()

        val parsed = mapper.readValue(json, SaraminApiResponse::class.java)
        assertNull(parsed.code)
        assertNotNull(parsed.jobs)
        assertEquals(110, parsed.jobs!!.count)
        assertEquals(0, parsed.jobs.start)
        assertEquals("1234", parsed.jobs.total)
        assertEquals(1, parsed.jobs.job.size)

        val job = parsed.jobs.job.first()
        assertEquals("46123456", job.id)
        assertEquals("삼성전자", job.company?.detail?.name)
        assertEquals("2026 상반기 신입공채", job.position?.title)
        assertEquals("IT개발·데이터", job.position?.jobMidCode?.name)
        assertEquals("1716595200", job.postingTimestamp)
        assertEquals("1718064000", job.expirationTimestamp)

        val domain = SaraminMapper.toDomain(job)
        assertNotNull(domain)
        assertEquals("saramin-46123456", domain!!.externalId)
    }

    @Test
    fun `사람인 에러 응답을 파싱한다`() {
        val json = """{"code": 4, "message": "일일 최대 요청 초과"}"""
        val parsed = mapper.readValue(json, SaraminApiResponse::class.java)
        assertEquals(4, parsed.code)
        assertEquals("일일 최대 요청 초과", parsed.message)
        assertNull(parsed.jobs)
    }

    @Test
    fun `빈 jobs 응답을 파싱한다`() {
        val json = """{"jobs": {"count": 0, "start": 0, "total": "0", "job": []}}"""
        val parsed = mapper.readValue(json, SaraminApiResponse::class.java)
        assertNotNull(parsed.jobs)
        assertEquals(0, parsed.jobs!!.job.size)
    }

    @Test
    fun `알 수 없는 필드는 무시한다`() {
        val json = """
        {
          "jobs": {"count": 0, "total": "0", "job": [], "wow_brand_new_field": "x"},
          "unknown_top_level": 123
        }
        """.trimIndent()
        // 예외 안 던지면 통과.
        mapper.readValue(json, SaraminApiResponse::class.java)
    }
}
