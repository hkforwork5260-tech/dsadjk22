package com.jobalert.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

private enum class WidgetSize(val label: String, val widthRatio: Float, val heightDp: Dp) {
    Small("Small (2×1)", 0.45f, 80.dp),
    Medium("Medium (4×2)", 1f, 110.dp),
    Large("Large (4×4)", 1f, 200.dp),
}

/**
 * 바탕화면 위젯 설정.
 * v0.1: 미리보기 + 크기 선택 UI만. 실제 AppWidgetProvider 등록은 v0.2.
 */
@Composable
fun WidgetSettingsScreen(onBack: () -> Unit) {
    var size by remember { mutableStateOf(WidgetSize.Medium) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "바탕화면 위젯",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 48.dp, expression = MascotExpression.Happy)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "바탕화면에서 바로 확인",
                        style = HiFiType.display.copy(fontSize = 20.sp),
                        color = HiFiColors.Text,
                    )
                    Text(
                        "오늘 새 공고 수를 한눈에",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // 미리보기 (어두운 바탕)
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1A1A1A))
                    .padding(18.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(size.widthRatio)
                        .height(size.heightDp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(HiFiColors.Bg)
                        .padding(12.dp),
                ) {
                    WidgetContent(size)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "위젯 크기",
                style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = HiFiColors.Text2,
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WidgetSize.values().forEach { opt ->
                    HiFiButton(
                        text = opt.label,
                        onClick = { size = opt },
                        variant = if (size == opt) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        fullWidth = true,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text(
                "홈 화면에 추가하는 법",
                style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = HiFiColors.Text2,
            )
            Spacer(Modifier.height(8.dp))
            GuideStep(1, "홈 화면 빈 곳을 길게 누르세요")
            GuideStep(2, "하단 메뉴에서 '위젯'을 선택하세요")
            GuideStep(3, "JobAlert 위젯을 골라 홈 화면에 추가하세요")

            Spacer(Modifier.height(22.dp))
            HiFiButton(
                text = "지금 추가하기",
                onClick = { /* v0.2 — AppWidgetManager.requestPinAppWidget */ },
                variant = HiFiButtonVariant.Primary,
                size = HiFiButtonSize.Lg,
                fullWidth = true,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "* v0.2에서 한 번 탭으로 추가 가능해질 예정이에요",
                style = HiFiType.body2.copy(fontSize = 11.sp),
                color = HiFiColors.Text3,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        HiFiGestureNav()
    }
}

@Composable
private fun WidgetContent(size: WidgetSize) {
    when (size) {
        WidgetSize.Small -> Row(verticalAlignment = Alignment.CenterVertically) {
            Mascot(size = 32.dp, expression = MascotExpression.Happy)
            Spacer(Modifier.width(8.dp))
            Column {
                Text("새 공고", style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
                Text("17", style = HiFiType.monoNum.copy(fontSize = 22.sp), color = HiFiColors.Brand)
            }
        }

        WidgetSize.Medium -> Row(verticalAlignment = Alignment.CenterVertically) {
            Mascot(size = 40.dp, expression = MascotExpression.Happy)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("오늘 새 공고", style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
                Row(verticalAlignment = Alignment.Bottom) {
                    Text("17", style = HiFiType.monoNum.copy(fontSize = 28.sp), color = HiFiColors.Brand)
                    Spacer(Modifier.width(6.dp))
                    Text("+ 마감 3", style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
                }
            }
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(HiFiColors.Brand)
                    .border(2.dp, Color.White, CircleShape)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
            ) {
                Text("17", style = HiFiType.caption.copy(fontSize = 11.sp), color = Color.White)
            }
        }

        WidgetSize.Large -> Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 40.dp, expression = MascotExpression.Happy)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("오늘 새 공고", style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
                    Text("17", style = HiFiType.monoNum.copy(fontSize = 28.sp), color = HiFiColors.Brand)
                }
            }
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
            Spacer(Modifier.height(8.dp))
            JobLine("토스 · 백엔드 개발자", "D-3", HiFiColors.Closing)
            Spacer(Modifier.height(4.dp))
            JobLine("카카오 · iOS 엔지니어", "D-7", HiFiColors.Update)
            Spacer(Modifier.height(4.dp))
            JobLine("네이버 · 디자이너", "NEW", HiFiColors.New)
        }
    }
}

@Composable
private fun JobLine(title: String, badge: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            title,
            style = HiFiType.body2.copy(fontSize = 11.sp),
            color = HiFiColors.Text,
            modifier = Modifier.weight(1f),
            maxLines = 1,
        )
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color)
                .padding(horizontal = 5.dp, vertical = 2.dp),
        ) {
            Text(badge, style = HiFiType.caption.copy(fontSize = 9.sp), color = Color.White)
        }
    }
}

@Composable
private fun GuideStep(num: Int, text: String) {
    Row(
        Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(HiFiColors.BrandSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "$num",
                style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                color = HiFiColors.BrandDark,
            )
        }
        Spacer(Modifier.width(10.dp))
        Text(text, style = HiFiType.body, color = HiFiColors.Text)
    }
}
