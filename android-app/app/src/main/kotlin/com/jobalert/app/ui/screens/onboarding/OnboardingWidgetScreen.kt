package com.jobalert.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 온보딩 ④ 위젯·알림 권한.
 * HiFi_Onb4Widget 대응. 위젯 미리보기 + 체크리스트 + 알림허용 CTA.
 */
@Composable
fun OnboardingWidgetScreen(
    onAllow: () -> Unit,
    onLater: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 14.dp),
        ) {
            OnboardingDots(total = 4, activeIndex = 3)

            Spacer(Modifier.height(18.dp))
            Text(
                "마지막!\n잊지 않게 챙겨줄게",
                style = HiFiType.display.copy(fontSize = 28.sp, lineHeight = 32.sp),
                color = HiFiColors.Text,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "매일 아침 9시, 저녁 9시에 새 공고를 알려드릴게요",
                style = HiFiType.body2,
                color = HiFiColors.Text2,
            )

            Spacer(Modifier.height(22.dp))
            // 위젯 미리보기 카드 (어두운 바탕화면 톤)
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(HiFiColors.BrandSoft)
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        "📱 바탕화면 위젯 미리보기",
                        style = HiFiType.caption,
                        color = HiFiColors.BrandDark,
                    )
                    Spacer(Modifier.height(10.dp))
                    // 검정 바탕 영역
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1A1A1A))
                            .padding(14.dp),
                    ) {
                        WidgetPreview()
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            // 체크리스트
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                BulletRow("아침 9:00 · 새 공고 요약")
                BulletRow("저녁 9:00 · 마감 임박 공고")
                BulletRow("바탕화면 위젯으로 한눈에")
            }

            Spacer(Modifier.weight(1f))

            HiFiButton(
                text = "알림 허용하고 위젯 추가",
                onClick = onAllow,
                variant = HiFiButtonVariant.Primary,
                size = HiFiButtonSize.Lg,
                fullWidth = true,
            )
            Spacer(Modifier.height(6.dp))
            HiFiButton(
                text = "나중에 설정에서 켤 수 있어요",
                onClick = onLater,
                variant = HiFiButtonVariant.Ghost,
                size = HiFiButtonSize.Sm,
                fullWidth = true,
            )
        }
        HiFiGestureNav()
    }
}

@Composable
private fun WidgetPreview() {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HiFiColors.Bg)
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Mascot(size = 40.dp, expression = MascotExpression.Happy)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text("오늘 새 공고", style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "17",
                        style = HiFiType.monoNum.copy(fontSize = 26.sp, lineHeight = 26.sp),
                        color = HiFiColors.Brand,
                    )
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
                contentAlignment = Alignment.Center,
            ) {
                Text("17", style = HiFiType.caption.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = Color.White)
            }
        }
    }
}

@Composable
private fun BulletRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(HiFiColors.Brand),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(text, style = HiFiType.body.copy(fontWeight = FontWeight.SemiBold), color = HiFiColors.Text)
    }
}
