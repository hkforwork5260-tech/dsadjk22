package com.jobalert.app.data.sample

import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.theme.JobKind

/**
 * 메인 피드용 하드코딩 샘플 (백엔드 붙기 전까지).
 */
val SampleJobs: List<Job> = listOf(
    Job(
        id = "samsung-2026-h1",
        company = "삼성전자", logo = "삼성",
        role = "2026 상반기 신입공채",
        kind = JobKind.NEW,
        dday = "D-24", dateText = "~6/15",
        location = "수원", experience = "신입", education = "학사+",
        tags = listOf("반도체", "DS", "신입공채"),
    ),
    Job(
        id = "naver-backend",
        company = "네이버", logo = "N",
        role = "신입 백엔드 개발자",
        kind = JobKind.NEW,
        dday = "D-19", dateText = "~6/10",
        location = "판교", experience = "신입", education = "학사+",
        tags = listOf("Java", "Kotlin", "Spring"),
        originalUrl = "recruit.navercorp.com/...",
    ),
    Job(
        id = "lges-rnd",
        company = "LG에너지솔루션", logo = "LG",
        role = "연구개발(R&D) 신입",
        kind = JobKind.NEW,
        dday = "D-16", dateText = "~6/7",
        location = "대전", experience = "신입", education = "학사+",
        tags = listOf("배터리", "R&D"),
    ),
    Job(
        id = "kakao-android",
        company = "카카오", logo = "카카오",
        role = "신입 안드로이드 개발자",
        kind = JobKind.NEW,
        dday = "D-14", dateText = "~6/5",
        location = "판교",
        tags = listOf("Android", "Kotlin", "Compose"),
    ),
    Job(
        id = "posco-2026-h1",
        company = "포스코", logo = "포스",
        role = "2026 상반기 신입공채",
        kind = JobKind.NEW,
        dday = "D-12", dateText = "~6/3",
        location = "포항",
    ),
    Job(
        id = "hmobis-rnd",
        company = "현대모비스", logo = "HM",
        role = "기계 R&D 신입",
        kind = JobKind.NEW,
        dday = "D-10", dateText = "~6/1",
        location = "용인",
    ),
    Job(
        id = "kakao-fe-update",
        company = "카카오", logo = "카",
        role = "경력 프론트엔드 개발자",
        kind = JobKind.UPDATE,
        dday = "D-6", dateText = "~5/28",
        location = "판교",
    ),
    Job(
        id = "hyundai-closing",
        company = "현대자동차", logo = "현",
        role = "신입사원 일반공채",
        kind = JobKind.CLOSING,
        dday = "D-1", dateText = "내일",
        location = "서울",
    ),
)
