package com.jobalert.backend.client.source.workday

import com.jobalert.backend.client.source.WorkdayTenant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class WorkdayMapperTest {

    private val daewoong = WorkdayTenant("daewoong.impl-wd102", "daewoong", "External", "대웅제약", homepage = "daewoong.com")

    @Test
    fun `대웅 - bulletFields가 외부ID, externalPath로 원문 URL`() {
        val p = WorkdayJobPosting(
            title = "ETC로컬 MR 경력직(대전 중구) (~7/10)",
            externalPath = "/job/KOR-Daejeon/ETC-MR------7-10-_R-26-1815-2",
            locationsText = "KOR-Daejeon",
            timeType = "Full time",
            bulletFields = listOf("R-26-1815"),
        )
        val raw = WorkdayMapper.toRawJob(daewoong, p)
        assertNotNull(raw)
        assertEquals("workday", raw.source)
        assertEquals("workday-daewoong-R-26-1815", raw.externalId)
        assertEquals("대웅제약", raw.companyName)
        assertEquals("KOR-Daejeon", raw.location)
        assertEquals(
            "https://daewoong.impl-wd102.myworkdayjobs.com/External/job/KOR-Daejeon/ETC-MR------7-10-_R-26-1815-2",
            raw.originalUrl,
        )
    }

    @Test
    fun `bulletFields 없으면 externalPath 마지막 세그먼트를 키로`() {
        val raw = WorkdayMapper.toRawJob(
            daewoong,
            WorkdayJobPosting(title = "QA", externalPath = "/job/Seoul/QA_R-99", bulletFields = emptyList()),
        )
        assertNotNull(raw)
        assertEquals("workday-daewoong-QA_R-99", raw.externalId)
    }

    @Test
    fun `title이나 path 없으면 null`() {
        assertNull(WorkdayMapper.toRawJob(daewoong, WorkdayJobPosting(title = null, externalPath = "/x")))
        assertNull(WorkdayMapper.toRawJob(daewoong, WorkdayJobPosting(title = "x", externalPath = null)))
    }
}
