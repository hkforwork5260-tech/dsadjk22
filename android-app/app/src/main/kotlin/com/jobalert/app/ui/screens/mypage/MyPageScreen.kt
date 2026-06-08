package com.jobalert.app.ui.screens.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.SeenJobs
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 마이페이지.
 * HiFi_MyPage 대응. 상단 streak/스탯 카드 + 메뉴 리스트.
 * 서브 화면들은 v0.1에서 placeholder 유지.
 */
@Composable
fun MyPageScreen(
    onNotifHistory: () -> Unit,
    onCalendar: () -> Unit,
    onNotifSettings: () -> Unit,
    onWidgetSettings: () -> Unit,
    onInterests: () -> Unit,
    onFeedback: () -> Unit,
    onSavedJobs: () -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "내 정보",
            action = {
                // 설정(톱니바퀴)은 미구현이라 제거. 알림 히스토리만 노출.
                HiFiIconBtn(Icons.Outlined.NotificationsNone, "알림", onClick = onNotifHistory)
            },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            // 상단 streak 카드 (코랄 톤)
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(HiFiColors.BrandSoft)
                    .border(2.dp, HiFiColors.Brand, RoundedCornerShape(18.dp))
                    .padding(18.dp),
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Mascot(size = 72.dp, expression = MascotExpression.Happy)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                "잘하고 있어요! 🔥",
                                style = HiFiType.title,
                                color = HiFiColors.BrandDark,
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "꽁이가 12일째 챙겨주는 중",
                                style = HiFiType.body2,
                                color = HiFiColors.Text2,
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Brand.copy(alpha = 0.35f)))
                    Spacer(Modifier.height(14.dp))
                    Row {
                        StatColumn(value = "${SeenJobs.seenIds.size}", label = "본 공고", color = HiFiColors.Text, modifier = Modifier.weight(1f))
                        Box(Modifier.width(1.dp).height(44.dp).background(HiFiColors.Brand.copy(alpha = 0.35f)))
                        StatColumn(
                            value = "🔖",
                            label = "저장한 공고 ›",
                            color = HiFiColors.UpdateShadow,
                            modifier = Modifier.weight(1f).clickable { onSavedJobs() },
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            // 메뉴 리스트
            // 부제(세부설명)는 제거 — 제목만 깔끔하게.
            val menu = listOf(
                MenuItem("🔔", "알림 설정", "", onClick = onNotifSettings),
                MenuItem("📜", "알림 히스토리", "", onClick = onNotifHistory),
                MenuItem("📅", "마감 캘린더", "", onClick = onCalendar),
                MenuItem("📱", "바탕화면 위젯", "", onClick = onWidgetSettings),
                MenuItem("🎯", "관심", "", onClick = onInterests),
                MenuItem("💬", "피드백 보내기", "", onClick = onFeedback),
                MenuItem("ℹ️", "앱 정보", "", onClick = null),
            )
            Column {
                menu.forEachIndexed { idx, item ->
                    MenuRow(item)
                    if (idx < menu.lastIndex) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
                    }
                }
            }
        }

        HiFiTabBar(active = HomeTab.Me, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

private data class MenuItem(
    val emoji: String,
    val title: String,
    val sub: String,
    val onClick: (() -> Unit)?,
)

@Composable
private fun StatColumn(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(value, style = HiFiType.monoNum.copy(fontSize = 26.sp), color = color)
        Spacer(Modifier.height(2.dp))
        Text(label, style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
    }
}

@Composable
private fun MenuRow(item: MenuItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (item.onClick != null) Modifier.clickable { item.onClick.invoke() } else Modifier)
            .padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(item.emoji, style = HiFiType.h2.copy(fontSize = 22.sp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(item.title, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text)
            if (item.sub.isNotBlank()) {
                Text(item.sub, style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
            }
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = HiFiColors.Text3,
            modifier = Modifier.size(16.dp),
        )
    }
}
