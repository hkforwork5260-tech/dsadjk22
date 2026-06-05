package com.jobalert.backend.client.source.publicinst

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 기재부 공공기관 채용 API의 **실제 응답**(2026-06-05 라이브 호출본 발췌)을
 * 우리 DTO가 그대로 읽어내는지 검증. 필드명 오타·매칭 오류 방지.
 */
class PublicInstitutionParseTest {

    private val mapper = ObjectMapper()

    // apis.data.go.kr/1051000/recruitment/list 실 응답 1건 발췌 (값 일부 축약)
    private val realSample = """
        {
          "resultCode": 200,
          "resultMsg": "성공했습니다.",
          "totalCount": 110693,
          "result": [
            {
              "recrutPblntSn": 301389,
              "instNm": "한국문화관광연구원",
              "ncsCdNmLst": "연구",
              "hireTypeNmLst": "비정규직",
              "workRgnNmLst": "서울",
              "recrutSeNm": "신입",
              "recrutNope": 4,
              "pbancBgngYmd": "20260605",
              "pbancEndYmd": "20260619",
              "recrutPbancTtl": "한국문화관광연구원 위촉직원 채용 공고[2026-21]",
              "srcUrl": "https://www.kcti.re.kr/web/board/boardContentsView.do?board_id=19",
              "ongoingYn": "Y"
            }
          ]
        }
    """.trimIndent()

    @Test
    fun `실 응답을 DTO로 파싱한다`() {
        val res = mapper.readValue(realSample, PublicInstitutionResponse::class.java)

        assertEquals(200, res.resultCode)
        assertEquals(110693, res.totalCount)
        assertEquals(1, res.result.size)

        val job = res.result[0]
        assertEquals(301389L, job.recrutPblntSn)
        assertEquals("한국문화관광연구원", job.instNm)
        assertEquals("한국문화관광연구원 위촉직원 채용 공고[2026-21]", job.recrutPbancTtl)
        assertEquals("서울", job.workRgnNmLst)
        assertEquals("연구", job.ncsCdNmLst)
        assertEquals("신입", job.recrutSeNm)
        assertEquals("20260619", job.pbancEndYmd)
        assertNotNull(job.srcUrl)
        assertEquals("Y", job.ongoingYn)
    }
}
