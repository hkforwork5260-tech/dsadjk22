package com.jobalert.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.ui.components.HiFiAppBar
import com.jobalert.app.ui.components.HiFiGestureNav
import com.jobalert.app.ui.components.HiFiIconBtn
import com.jobalert.app.ui.components.HiFiStatusBar
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 알림 설정.
 * v0.1: 토글 UI만. 실제 FCM 채널 연동은 백엔드 Phase 3 이후.
 * 모든 토글 상태는 화면 로컬 (Persistence는 v0.2).
 */
@Composable
fun NotifSettingsScreen(onBack: () -> Unit) {
    val viewModel: NotifSettingsViewModel = viewModel()
    var pushEnabled by remember { mutableStateOf(true) }
    var morningEnabled by remember { mutableStateOf(true) }
    var eveningEnabled by remember { mutableStateOf(true) }
    // 아침/저녁 토글 변경 시 백엔드 기기 설정에 반영(전체 푸시 끄면 둘 다 off로 동기화).
    fun syncPush() = viewModel.setPush(morning = morningEnabled && pushEnabled, evening = eveningEnabled && pushEnabled)
    var closingEnabled by remember { mutableStateOf(true) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(false) }
    var fullScreenEnabled by remember { mutableStateOf(true) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "알림 설정",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            Section("푸시 알림")
            ToggleRow(
                title = "전체 푸시 알림",
                sub = "끄면 모든 알림이 차단돼요",
                checked = pushEnabled,
                onCheckedChange = { pushEnabled = it; syncPush() },
            )

            Spacer(Modifier.height(18.dp))
            Section("알림 시간")
            ToggleRow(
                title = "🌅 아침 9:00",
                sub = "새로 올라온 공고 요약",
                checked = morningEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { morningEnabled = it; syncPush() },
            )
            Divider()
            ToggleRow(
                title = "🌙 저녁 9:00",
                sub = "마감 임박 공고",
                checked = eveningEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { eveningEnabled = it; syncPush() },
            )
            Divider()
            ToggleRow(
                title = "⚡ 마감 임박 알림",
                sub = "관심기업 공고 마감 24시간 전",
                checked = closingEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { closingEnabled = it },
            )

            Spacer(Modifier.height(18.dp))
            Section("알림 방식")
            ToggleRow(
                title = "🔊 소리",
                sub = "알림 도착 시 효과음",
                checked = soundEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { soundEnabled = it },
            )
            Divider()
            ToggleRow(
                title = "📳 진동",
                sub = "무음 모드에서도 진동",
                checked = vibrationEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { vibrationEnabled = it },
            )
            Divider()
            ToggleRow(
                title = "🖥️ 잠금화면 풀스크린",
                sub = "알림을 큰 화면으로 표시",
                checked = fullScreenEnabled && pushEnabled,
                enabled = pushEnabled,
                onCheckedChange = { fullScreenEnabled = it },
            )

            Spacer(Modifier.height(22.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.BrandSoft)
                    .padding(14.dp),
            ) {
                Text(
                    "💡 푸시 알림 시간은 고정이에요.\n" +
                        "매일 같은 시간에 와야 습관이 만들어진다는 듀오링고 원칙을 적용했어요.",
                    style = HiFiType.body2.copy(fontSize = 13.sp),
                    color = HiFiColors.BrandDark,
                )
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun Section(title: String) {
    Spacer(Modifier.height(14.dp))
    Text(
        title,
        style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
        color = HiFiColors.Text2,
    )
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun ToggleRow(
    title: String,
    sub: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text)
            if (sub.isNotBlank()) {
                Text(sub, style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = HiFiColors.Brand,
                checkedBorderColor = HiFiColors.Brand,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = HiFiColors.Bg3,
                uncheckedBorderColor = HiFiColors.Border,
            ),
        )
    }
}

@Composable
private fun Divider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
}
