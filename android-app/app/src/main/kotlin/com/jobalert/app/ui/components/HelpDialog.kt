package com.jobalert.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 처음 진입 시 보여주는 도움말 다이얼로그. 마스코트 + 제목 + 글머리 설명 + 확인 버튼.
 * 화면별로 [com.jobalert.app.data.HelpState]로 1회만 자동 노출하고, 앱바 '?'로 다시 열 수 있다.
 */
@Composable
fun HelpDialog(
    title: String,
    lines: List<String>,
    expression: MascotExpression = MascotExpression.Wave,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(HiFiColors.Bg2)
                .padding(22.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 52.dp, expression = expression)
                Spacer(Modifier.width(12.dp))
                Text(title, style = HiFiType.title, color = HiFiColors.Text)
            }
            Spacer(Modifier.height(16.dp))
            lines.forEach { line ->
                Row(Modifier.padding(vertical = 5.dp)) {
                    Box(
                        Modifier.padding(top = 7.dp).size(6.dp).clip(CircleShape).background(HiFiColors.Brand),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        line,
                        style = HiFiType.body.copy(lineHeight = 21.sp),
                        color = HiFiColors.Text,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            HiFiButton(
                text = "알겠어요",
                onClick = onDismiss,
                variant = HiFiButtonVariant.Primary,
                fullWidth = true,
            )
        }
    }
}

/** 앱바 등에 두는 '?' 도움말 버튼. */
@Composable
fun HelpIconButton(onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(HiFiColors.Bg3)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            "?",
            style = HiFiType.body.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            color = HiFiColors.Text2,
        )
    }
}
