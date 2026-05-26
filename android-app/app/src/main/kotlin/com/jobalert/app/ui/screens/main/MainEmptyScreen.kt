package com.jobalert.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.api.MockApi
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*

/**
 * 메인 빈 상태. NEW 0건일 때 노출.
 * HiFi_MainEmpty 대응. "오늘은 조용한 날" 헤더 + 빈 상태 카드 + 챙겨봐야 할 공고 섹션.
 */
@Composable
fun MainEmptyScreen(
    onJobClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onAddFavorites: () -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    val today = remember { MockApi.jobsTodayEmpty() }
    var section by remember { mutableStateOf(JobKind.NEW) }
    val counts = today.counts

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "채용알리미",
            action = {
                Row {
                    HiFiIconBtn(Icons.Outlined.NotificationsNone, "알림", onClick = onNotificationClick)
                    Spacer(Modifier.width(6.dp))
                    HiFiIconBtn(Icons.Outlined.Tune, "필터", onClick = onFilterClick)
                }
            },
        )

        // 헤더
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Column(Modifier.weight(1f)) {
                Text("5월 22일 목요일", style = HiFiType.body2, color = HiFiColors.Text2)
                Spacer(Modifier.height(2.dp))
                Row {
                    Text("오늘은 ", style = HiFiType.title, color = HiFiColors.Text)
                    Text("조용한 날", style = HiFiType.title, color = HiFiColors.Text2)
                }
            }
            Mascot(size = 60.dp, expression = MascotExpression.Sleep)
        }

        // 세 토글 (NEW 0 회색)
        Row(
            Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            JobKind.values().forEach { kind ->
                val c = when (kind) {
                    JobKind.NEW -> counts["new"] ?: 0
                    JobKind.UPDATE -> counts["update"] ?: 0
                    JobKind.CLOSING -> counts["closing"] ?: 0
                }
                EmptySectionChip(
                    kind = kind,
                    count = c,
                    selected = section == kind,
                    onClick = { section = kind },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // 본문 스크롤
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            // 빈 상태 카드 (코랄 톤)
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(HiFiColors.BrandSoft)
                    .border(2.dp, HiFiColors.Brand, RoundedCornerShape(18.dp))
                    .padding(22.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Mascot(size = 80.dp, expression = MascotExpression.Default)
                    Spacer(Modifier.height(10.dp))
                    Text("오늘 새 공고는 없어요", style = HiFiType.h2, color = HiFiColors.Text)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "대부분 기업이 휴식 중이에요.\n관심 기업을 더 추가하면 더 자주 볼 수 있어요!",
                        style = HiFiType.body2.copy(lineHeight = 19.sp),
                        color = HiFiColors.Text2,
                    )
                    Spacer(Modifier.height(16.dp))
                    HiFiButton(
                        text = "+ 관심 기업 추가",
                        onClick = onAddFavorites,
                        variant = HiFiButtonVariant.Primary,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("📌 챙겨봐야 할 공고", style = HiFiType.h2, color = HiFiColors.Text)
            Spacer(Modifier.height(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                today.jobs.forEach { job ->
                    HiFiJobCard(
                        kind = kindOf(job.kind),
                        company = job.company.name,
                        role = job.title,
                        logo = job.company.logo,
                        dday = job.dday,
                        dateText = displayDate(job.dday),
                        onClick = { onJobClick(job.id) },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            // 다음 자동 수집 안내
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = HiFiColors.Text2,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "다음 자동 수집: 내일 9:00",
                        style = HiFiType.body2.copy(fontWeight = FontWeight.Bold),
                        color = HiFiColors.Text2,
                    )
                }
            }
        }

        HiFiTabBar(active = HomeTab.Home, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

@Composable
private fun EmptySectionChip(
    kind: JobKind,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // count == 0 이면 회색, 아니면 일반 outline
    val isZero = count == 0
    val bg = if (selected && !isZero) kind.color() else HiFiColors.Bg
    val fg = when {
        selected && !isZero -> Color.White
        isZero -> HiFiColors.Text3
        else -> HiFiColors.Text2
    }
    val border = when {
        selected && !isZero -> kind.color()
        else -> HiFiColors.Border
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .border(2.dp, border, CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("${kind.label()} $count", style = HiFiType.body2.copy(fontSize = 14.sp), color = fg)
    }
}

private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    "UPDATE" -> JobKind.UPDATE
    "CLOSING" -> JobKind.CLOSING
    else -> JobKind.NEW
}

/** D-day 문자열에서 사용자 친화 날짜 (간이 변환) */
private fun displayDate(dday: String): String = when (dday) {
    "D-0" -> "오늘"
    "D-1" -> "내일"
    else -> ""
}
