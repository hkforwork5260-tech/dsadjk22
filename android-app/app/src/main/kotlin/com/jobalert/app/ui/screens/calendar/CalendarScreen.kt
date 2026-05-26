package com.jobalert.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.api.JobDto
import com.jobalert.app.data.api.MockApi
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*

/**
 * 마감 캘린더 (기본형).
 * HiFi_Calendar 대응. 월 그리드 + 마감일 셀에 회사 라벨 + 오늘/이번주 마감 리스트.
 *
 * v0.1는 2026년 5월 고정. 월 이동(< >) 버튼은 v0.2로.
 */
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
) {
    val upcoming = remember { MockApi.upcoming() }
    // 캘린더 데이터를 일 → JobDto 리스트로 변환 (2026년 5/6월만)
    val byDay: Map<Int, List<JobDto>> = remember(upcoming) {
        upcoming.byDate
            .filterKeys { it.startsWith("2026-05") }
            .mapKeys { it.key.substringAfterLast('-').toInt() }
    }
    val today = 26
    val totalDays = 31
    val firstDow = 5  // 2026-05-01 = 금요일 (일=0,월=1,...,금=5,토=6)

    // 6주 × 7일 = 42셀
    val cells = (0 until 42).map { i ->
        val d = i - firstDow + 1
        if (d in 1..totalDays) d else null
    }

    val todayJobs = byDay[today] ?: emptyList()
    val thisWeekJobs = (today + 1..today + 7).flatMap { byDay[it] ?: emptyList() }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "마감 캘린더",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
            action = { HiFiIconBtn(Icons.Outlined.Settings, "설정", onClick = { /* TODO */ }) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            // 월 헤더 (< 2026년 5월 >)
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.ChevronLeft, contentDescription = "이전 달", tint = HiFiColors.Text2, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(16.dp))
                Text("2026년 5월", style = HiFiType.h2, color = HiFiColors.Text)
                Spacer(Modifier.width(16.dp))
                Icon(Icons.Outlined.ChevronRight, contentDescription = "다음 달", tint = HiFiColors.Text2, modifier = Modifier.size(20.dp))
            }

            // 요일 헤더
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val days = listOf("일", "월", "화", "수", "목", "금", "토")
                days.forEachIndexed { i, d ->
                    Text(
                        d,
                        style = HiFiType.caption.copy(fontSize = 11.sp, letterSpacing = 0.sp),
                        color = when (i) {
                            0 -> HiFiColors.Closing
                            6 -> HiFiColors.Info
                            else -> HiFiColors.Text2
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            // 날짜 그리드 (6주)
            for (week in 0 until 6) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    for (dow in 0 until 7) {
                        val idx = week * 7 + dow
                        val d = cells[idx]
                        CalendarCell(
                            day = d,
                            isToday = d == today,
                            dow = dow,
                            jobs = if (d != null) byDay[d].orEmpty() else emptyList(),
                            onClick = { d?.let { _ -> /* TODO: 일 선택 highlight */ } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // 범례
            Row(
                Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                LegendDot(HiFiColors.New, "새공고")
                Spacer(Modifier.width(12.dp))
                LegendDot(HiFiColors.Update, "변경")
                Spacer(Modifier.width(12.dp))
                LegendDot(HiFiColors.Closing, "마감")
            }

            // 오늘 마감
            Spacer(Modifier.height(22.dp))
            if (todayJobs.isNotEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HiFiColors.BrandSoft)
                        .border(2.dp, HiFiColors.Brand, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Column {
                        Text("오늘 마감 · 5/26", style = HiFiType.caption, color = HiFiColors.BrandDark)
                        Spacer(Modifier.height(4.dp))
                        Text("${todayJobs.size}개 공고", style = HiFiType.h2, color = HiFiColors.Text)
                        Spacer(Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            todayJobs.forEach { job ->
                                HiFiJobCard(
                                    kind = kindOf(job.kind),
                                    company = job.company.name,
                                    role = job.title,
                                    logo = job.company.logo,
                                    dday = job.dday,
                                    dateText = "23:59",
                                    onClick = { onJobClick(job.id) },
                                )
                            }
                        }
                    }
                }
            }

            // 이번 주 마감
            if (thisWeekJobs.isNotEmpty()) {
                Spacer(Modifier.height(22.dp))
                Text("이번 주 마감 (${thisWeekJobs.size}건)", style = HiFiType.h2, color = HiFiColors.Text)
                Spacer(Modifier.height(10.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    thisWeekJobs.forEach { job ->
                        HiFiJobCard(
                            kind = kindOf(job.kind),
                            company = job.company.name,
                            role = job.title,
                            logo = job.company.logo,
                            dday = job.dday,
                            dateText = displayDeadlineShort(job.deadline),
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun CalendarCell(
    day: Int?,
    isToday: Boolean,
    dow: Int,
    jobs: List<JobDto>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasJobs = jobs.isNotEmpty()
    val base = modifier
        .aspectRatio(1f / 1.2f)
        .clip(RoundedCornerShape(8.dp))
        .background(if (hasJobs) HiFiColors.BrandSoft else Color.Transparent)
    val bordered = if (isToday) base.border(2.dp, HiFiColors.Brand, RoundedCornerShape(8.dp)) else base
    Column(
        bordered
            .clickable(enabled = day != null, onClick = onClick)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (day != null) {
            Text(
                "$day",
                style = HiFiType.body2.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.SemiBold,
                ),
                color = when {
                    isToday -> HiFiColors.Brand
                    dow == 0 -> HiFiColors.Closing
                    dow == 6 -> HiFiColors.Info
                    else -> HiFiColors.Text
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            // 최대 2개 라벨 노출
            jobs.take(2).forEach { job ->
                CellLabel(job)
            }
            if (jobs.size > 2) {
                Text(
                    "+${jobs.size - 2}",
                    style = HiFiType.caption.copy(fontSize = 8.sp, letterSpacing = 0.sp),
                    color = HiFiColors.Text2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun CellLabel(job: JobDto) {
    val k = kindOf(job.kind)
    val bg = when (k) {
        JobKind.NEW -> HiFiColors.New
        JobKind.UPDATE -> HiFiColors.Update
        JobKind.CLOSING -> HiFiColors.Closing
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 3.dp, vertical = 1.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            job.company.logo,
            style = HiFiType.caption.copy(fontSize = 9.sp, letterSpacing = 0.sp),
            color = Color.White,
            maxLines = 1,
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color),
        )
        Spacer(Modifier.width(4.dp))
        Text(label, style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
    }
}

private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    "UPDATE" -> JobKind.UPDATE
    "CLOSING" -> JobKind.CLOSING
    else -> JobKind.NEW
}

private fun displayDeadlineShort(iso: String): String {
    val date = iso.substringBefore('T')
    val parts = date.split('-')
    if (parts.size < 3) return ""
    val m = parts[1].trimStart('0')
    val d = parts[2].trimStart('0')
    return "$m/$d"
}
