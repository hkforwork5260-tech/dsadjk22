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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.jobalert.app.data.HelpState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.JobDto
import com.jobalert.app.data.api.UpcomingResponse
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

/**
 * 마감 캘린더 (기본형).
 * HiFi_Calendar 대응. 월 그리드 + 마감일 셀에 회사 라벨 + 오늘/이번주 마감 리스트.
 *
 * 현재 달 기준(백엔드 /jobs/upcoming 연결됨). 월 이동(< >) 버튼만 미구현.
 */
@Composable
fun CalendarScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
) {
    // 백엔드 /jobs/upcoming(40일) 연결. 로딩·에러 시엔 빈 맵으로 렌더.
    val viewModel: CalendarViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val upcoming = (state as? CalendarUiState.Success)?.data
        ?: UpcomingResponse(days = 40, byDate = emptyMap())

    // 현재 달 기준으로 그리드 구성 (KST). 디자인 단계의 2026-05 하드코딩을 일반화.
    val nowDate = remember { LocalDate.now(ZoneId.of("Asia/Seoul")) }
    val curMonth = remember(nowDate) { YearMonth.from(nowDate) }
    // 보고 있는 달(< >로 이동). 데이터는 70일 확보 → 이번 달 + 다음 2달까지 의미 있음.
    var displayMonth by remember { mutableStateOf(curMonth) }
    val ym = displayMonth
    val isCurrentMonth = ym == curMonth
    val today = if (isCurrentMonth) nowDate.dayOfMonth else -1   // 다른 달이면 '오늘' 강조 없음
    val totalDays = ym.lengthOfMonth()
    // java.time: 월=1..일=7 → 디자인 요일(일=0,월=1,...,토=6)로 변환
    val firstDow = ym.atDay(1).dayOfWeek.value % 7
    val monthPrefix = "%04d-%02d".format(ym.year, ym.monthValue)

    // 마감일(byDate) → 해당 달의 일(day) → JobDto 리스트
    val byDay: Map<Int, List<JobDto>> = upcoming.byDate
        .filterKeys { it.startsWith(monthPrefix) }
        .mapKeys { it.key.substringAfterLast('-').toInt() }

    // 6주 × 7일 = 42셀
    val cells = (0 until 42).map { i ->
        val d = i - firstDow + 1
        if (d in 1..totalDays) d else null
    }

    // 선택한 날짜(기본=오늘). 셀을 누르면 그 날짜의 마감 공고를 아래에 보여준다.
    var selectedDay by remember(monthPrefix) { mutableStateOf(if (isCurrentMonth) today else 1) }
    val selectedJobs = byDay[selectedDay] ?: emptyList()

    val context = LocalContext.current
    var showHelp by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!HelpState.shown(context, "calendar")) { showHelp = true; HelpState.markShown(context, "calendar") }
    }
    if (showHelp) {
        HelpDialog(
            title = "마감 캘린더",
            lines = listOf(
                "날짜를 누르면 그날 마감되는 공고를 볼 수 있어요.",
                "위 < > 로 다음 달(최대 2개월)까지 확인하세요.",
                "마감이 지난 공고는 자동으로 사라져요.",
            ),
            onDismiss = { showHelp = false },
        )
    }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "마감 캘린더",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
            action = { HelpIconButton(onClick = { showHelp = true }) },
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
                val canPrev = displayMonth > curMonth                 // 지난 달은 의미 없어 막음
                val canNext = displayMonth < curMonth.plusMonths(2)   // 데이터(70일) 범위까지
                Icon(
                    Icons.Outlined.ChevronLeft, contentDescription = "이전 달",
                    tint = if (canPrev) HiFiColors.Text else HiFiColors.Border,
                    modifier = Modifier.size(28.dp)
                        .clickable(enabled = canPrev) { displayMonth = displayMonth.minusMonths(1) },
                )
                Spacer(Modifier.width(16.dp))
                Text("${ym.year}년 ${ym.monthValue}월", style = HiFiType.h2, color = HiFiColors.Text)
                Spacer(Modifier.width(16.dp))
                Icon(
                    Icons.Outlined.ChevronRight, contentDescription = "다음 달",
                    tint = if (canNext) HiFiColors.Text else HiFiColors.Border,
                    modifier = Modifier.size(28.dp)
                        .clickable(enabled = canNext) { displayMonth = displayMonth.plusMonths(1) },
                )
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
                            isSelected = d != null && d == selectedDay,
                            dow = dow,
                            count = if (d != null) byDay[d].orEmpty().size else 0,
                            onClick = { d?.let { selectedDay = it } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            // 선택한 날짜의 마감 공고 (셀을 누르면 갱신, 기본=오늘)
            Spacer(Modifier.height(22.dp))
            Text(
                "${ym.monthValue}/$selectedDay 마감",
                style = HiFiType.h2,
                color = HiFiColors.Text,
            )
            Spacer(Modifier.height(10.dp))
            if (selectedJobs.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    selectedJobs.forEach { job ->
                        HiFiJobCard(
                            kind = kindOf(job.kind),
                            company = job.company.name,
                            role = job.title,
                            logo = shortRegion(job.location, job.company.logo),   // 좌측=근무지역(홈과 통일)
                            dday = job.dday,
                            dateText = displayDeadlineShort(job.deadline),
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            } else {
                Text(
                    "이 날 마감되는 공고가 없어요",
                    style = HiFiType.body2,
                    color = HiFiColors.Text2,
                )
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun CalendarCell(
    day: Int?,
    isToday: Boolean,
    isSelected: Boolean,
    dow: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 선택일은 채운 강조, 오늘은 테두리. 둘 다 아니면 투명.
    val base = modifier
        .aspectRatio(1f / 1.2f)
        .clip(RoundedCornerShape(8.dp))
        .background(if (isSelected) HiFiColors.BrandSoft else Color.Transparent)
    val bordered = when {
        isSelected -> base.border(2.dp, HiFiColors.Brand, RoundedCornerShape(8.dp))
        isToday -> base.border(2.dp, HiFiColors.Brand.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
        else -> base
    }
    Column(
        bordered
            .clickable(enabled = day != null, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (day != null) {
            Text(
                "$day",
                style = HiFiType.body2.copy(
                    fontSize = 12.sp,
                    fontWeight = if (isToday || isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                ),
                color = when {
                    isToday || isSelected -> HiFiColors.Brand
                    dow == 0 -> HiFiColors.Closing
                    dow == 6 -> HiFiColors.Info
                    else -> HiFiColors.Text
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            // 그날 마감되는 공고 수만 표시(라벨칸 제거). 0이면 안 보임.
            if (count > 0) {
                Box(
                    Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(HiFiColors.Closing),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "$count",
                        style = HiFiType.caption.copy(fontSize = 10.sp, letterSpacing = 0.sp, fontWeight = FontWeight.Bold),
                        color = Color.White,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

// 캘린더에선 새로 생긴 공고만 NEW 배지. 미래 마감(내일 등)은 CLOSING으로 보지 않음 →
// NEW 외엔 ACTIVE(라벨 숨김). 날짜 지난 공고는 만료되어 애초에 안 들어온다.
private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    else -> JobKind.ACTIVE
}

/** JobDto.location → 짧은 근무지역(홈 카드와 동일 톤). 없으면 fallback(회사 약어). */
private fun shortRegion(location: String?, fallback: String): String {
    val loc = location?.takeIf { it.isNotBlank() } ?: return fallback
    val first = loc.split(",", "·").firstOrNull { it.isNotBlank() }?.trim()
        ?.split(" ")?.firstOrNull { it.isNotBlank() } ?: return fallback
    return first.removeSuffix("특별자치시").removeSuffix("특별자치도")
        .removeSuffix("특별시").removeSuffix("광역시").removeSuffix("도").removeSuffix("시")
        .ifBlank { fallback }.take(4)
}

private fun displayDeadlineShort(iso: String?): String {
    if (iso.isNullOrBlank()) return ""   // 상시채용(마감일 없음) 대응
    val date = iso.substringBefore('T')
    val parts = date.split('-')
    if (parts.size < 3) return ""
    val m = parts[1].trimStart('0')
    val d = parts[2].trimStart('0')
    return "$m/$d"
}
