package com.jobalert.app.ui.screens.notif

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.NotificationDto
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 알림 히스토리.
 * HiFi_NotifHistory 대응. 날짜 그룹핑 + 카드 (안 읽음=코랄 배경 + 우상단 dot).
 */
@Composable
fun NotifHistoryScreen(
    onBack: () -> Unit,
    onItemClick: (NotificationDto) -> Unit,
) {
    // 백엔드 알림 히스토리(기기 기준). 진입할 때마다 새로고침.
    val viewModel: NotifHistoryViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val initial = (state as? NotifUiState.Success)?.notifications ?: emptyList()
    // 로드되면 items 갱신. '모두 읽음' 등 읽음 토글은 로컬 시각 처리.
    var items by remember(initial) { mutableStateOf(initial) }

    // 날짜 그룹핑 (오늘 / 어제 / 이번 주 / 그 외)
    val grouped: List<Pair<String, List<NotificationDto>>> = remember(items) {
        groupNotifications(items)
    }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "알림",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
            action = {
                HiFiButton(
                    text = "모두 읽음",
                    onClick = { items = items.map { it.copy(read = true) } },
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            grouped.forEach { (group, notifs) ->
                Text(group, style = HiFiType.caption, color = HiFiColors.Text2)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    notifs.forEach { n ->
                        NotifCard(
                            n = n,
                            onClick = {
                                items = items.map { if (it.id == n.id) it.copy(read = true) else it }
                                onItemClick(n)
                            },
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
            }

            // "더 있어요" 가짜 카드
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .dashedBorder(HiFiColors.BorderDark)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("+ 31개 더 있어요", style = HiFiType.body2, color = HiFiColors.Text2)
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun NotifCard(n: NotificationDto, onClick: () -> Unit) {
    val unread = !n.read
    val bg = if (unread) HiFiColors.BrandSoft else HiFiColors.Bg
    val borderColor = if (unread) HiFiColors.Brand else HiFiColors.Border

    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // 아이콘 박스
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgFor(n.kind)),
                contentAlignment = Alignment.Center,
            ) {
                Text(iconEmojiFor(n.kind), fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        n.title,
                        style = HiFiType.body.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        color = HiFiColors.Text,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        displayTime(n.sentAt),
                        style = HiFiType.body2.copy(fontSize = 11.sp),
                        color = HiFiColors.Text2,
                    )
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    n.body,
                    style = HiFiType.body2.copy(fontSize = 13.sp, lineHeight = 18.sp),
                    color = HiFiColors.Text2,
                )
            }
        }
        if (unread) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 0.dp)
                    .offset(x = (-2).dp, y = 2.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(HiFiColors.Brand),
            )
        }
    }
}

private fun iconBgFor(kind: String): Color = when (kind) {
    "morning_digest" -> HiFiColors.BrandSoft
    "evening_digest" -> HiFiColors.UpdateSoft
    "deadline" -> HiFiColors.ClosingSoft
    else -> HiFiColors.InfoSoft
}

private fun iconEmojiFor(kind: String): String = when (kind) {
    "morning_digest" -> "☀️"
    "evening_digest" -> "🔥"
    "deadline" -> "🔥"
    else -> "🔔"
}

/**
 * "2026-05-26" → "오늘 · 5/26" 같은 그룹 라벨 + 정렬된 그룹 순서.
 * 데모용 단순 분류 — 실 운영은 백엔드 ZonedDateTime 변환 + KST.
 */
private fun groupNotifications(items: List<NotificationDto>): List<Pair<String, List<NotificationDto>>> {
    // 단순화: sentAt의 날짜 부분(YYYY-MM-DD)으로 묶음
    val byDate = items.groupBy { it.sentAt.substringBefore('T') }
    // 데모상 오늘=2026-05-26, 어제=2026-05-25
    val today = "2026-05-26"
    val yesterday = "2026-05-25"
    val result = mutableListOf<Pair<String, List<NotificationDto>>>()
    byDate[today]?.let { result += "오늘 · 5/26" to it }
    byDate[yesterday]?.let { result += "어제 · 5/25" to it }
    val rest = byDate.filterKeys { it != today && it != yesterday }
    if (rest.isNotEmpty()) {
        val all = rest.values.flatten()
        result += "이번 주" to all
    }
    return result
}

private fun displayTime(iso: String): String {
    // 단순 파싱: "2026-05-26T09:00:00Z" → "9:00"
    val t = iso.substringAfter('T').substringBefore('Z')
    val parts = t.split(':')
    if (parts.size < 2) return ""
    val h = parts[0].trimStart('0').ifBlank { "0" }
    return "$h:${parts[1]}"
}

/** 점선 테두리 (FavoritesScreen과 동일 로직, 소규모라 공유 컴포넌트화 안 함) */
private fun Modifier.dashedBorder(color: Color): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(14.dp.toPx()),
    )
}
