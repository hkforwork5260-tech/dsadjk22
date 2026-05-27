package com.jobalert.app.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

private val FeedbackCategories = listOf("🐛 버그", "💡 제안", "❤️ 칭찬", "❓ 기타")

/**
 * 피드백 보내기.
 * v0.1: 별점 + 카테고리 + 본문 + 이메일. 전송은 Toast로 완료 시뮬레이션.
 * 백엔드 Phase 3 이후 실 POST /v1/feedback 연결.
 */
@Composable
fun FeedbackScreen(onBack: () -> Unit) {
    var rating by remember { mutableIntStateOf(5) }
    var categoryIdx by remember { mutableStateOf(1) }
    var body by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "피드백 보내기",
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
                Mascot(
                    size = 48.dp,
                    expression = if (rating >= 4) MascotExpression.Happy else MascotExpression.Wave,
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "꽁이가 듣고 있어요",
                        style = HiFiType.display.copy(fontSize = 20.sp),
                        color = HiFiColors.Text,
                    )
                    Text(
                        "어떤 점이 좋았고, 뭐가 아쉬웠는지 알려주세요",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Label("앱 사용 만족도")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    Box(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (star <= rating) HiFiColors.BrandSoft else HiFiColors.Bg2)
                            .border(
                                1.dp,
                                if (star <= rating) HiFiColors.Brand else HiFiColors.Border,
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { rating = star }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (star <= rating) "⭐" else "☆",
                            style = HiFiType.h2.copy(fontSize = 22.sp),
                            color = if (star <= rating) HiFiColors.Brand else HiFiColors.Text3,
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Label("카테고리")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FeedbackCategories.forEachIndexed { idx, label ->
                    HiFiButton(
                        text = label,
                        onClick = { categoryIdx = idx },
                        variant = if (idx == categoryIdx) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                        fullWidth = true,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Label("자세히 알려주세요")
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .border(1.dp, HiFiColors.Border, RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                BasicTextField(
                    value = body,
                    onValueChange = { body = it },
                    textStyle = HiFiType.body.copy(color = HiFiColors.Text),
                    cursorBrush = SolidColor(HiFiColors.Brand),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (body.isEmpty()) {
                            Text(
                                "예: 아침 알림 시간을 8:30으로 바꿀 수 있으면 좋겠어요",
                                style = HiFiType.body,
                                color = HiFiColors.Text3,
                            )
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(14.dp))
            Label("이메일 (선택)")
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .border(1.dp, HiFiColors.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                BasicTextField(
                    value = email,
                    onValueChange = { email = it },
                    textStyle = HiFiType.body.copy(color = HiFiColors.Text),
                    cursorBrush = SolidColor(HiFiColors.Brand),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { inner ->
                        if (email.isEmpty()) {
                            Text("답장 받을 이메일 주소", style = HiFiType.body, color = HiFiColors.Text3)
                        }
                        inner()
                    },
                )
            }

            Spacer(Modifier.height(22.dp))
            HiFiButton(
                text = "보내기",
                onClick = {
                    Toast.makeText(context, "전송됐어요! 꽁이가 잘 읽어볼게요 ✨", Toast.LENGTH_LONG).show()
                    onBack()
                },
                variant = HiFiButtonVariant.Primary,
                size = HiFiButtonSize.Lg,
                fullWidth = true,
                enabled = body.isNotBlank(),
            )
        }

        HiFiGestureNav()
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text,
        style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
        color = HiFiColors.Text2,
    )
}
