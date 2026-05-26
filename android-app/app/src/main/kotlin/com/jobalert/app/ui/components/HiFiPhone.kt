package com.jobalert.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 안드로이드 시스템 상태바 영역. 디자인 일관성을 위해 앱 내부에서 30dp 영역을 그림.
 * 프로덕션에서는 시스템 상태바를 그대로 쓰고 이 컴포넌트는 제거 가능.
 */
@Composable
fun HiFiStatusBar(
    modifier: Modifier = Modifier,
    darkText: Boolean = true,
) {
    val color = if (darkText) HiFiColors.Text else Color.White
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("9:41", style = HiFiType.caption.copy(fontSize = 13.sp), color = color)
        Spacer(Modifier.weight(1f))
        Text("●●●", style = HiFiType.body2.copy(fontSize = 12.sp), color = color)
        Spacer(Modifier.width(6.dp))
        Text("📶", style = HiFiType.body2.copy(fontSize = 12.sp))
        Spacer(Modifier.width(6.dp))
        Box(
            Modifier
                .width(22.dp)
                .height(11.dp)
                .border(1.4f.dp, color, RoundedCornerShape(2.dp))
                .padding(1.dp),
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.8f)
                    .background(color)
            )
        }
    }
}

/**
 * 앱바: leading (선택) + title + action (선택). 높이 48dp 이상.
 */
@Composable
fun HiFiAppBar(
    title: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(12.dp))
        }
        Text(
            text = title,
            style = HiFiType.title,
            color = HiFiColors.Text,
            modifier = Modifier.weight(1f),
        )
        if (action != null) {
            action()
        }
    }
}

/**
 * 안드로이드 제스처 nav pill (화면 하단 가로선).
 */
@Composable
fun HiFiGestureNav(
    modifier: Modifier = Modifier,
    light: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .width(120.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (light) Color.White.copy(alpha = 0.8f) else HiFiColors.Text)
        )
    }
}
