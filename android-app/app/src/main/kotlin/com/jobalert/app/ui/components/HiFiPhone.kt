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
 * (프로토타입 잔재) 가짜 시스템 상태바였음 — 실기기에선 OS 상태바를 쓰고,
 * MainActivity 루트에서 systemBars 인셋을 이미 적용하므로 아무것도 그리지 않는다.
 * 호출부 23곳을 한 번에 정리하기 위해 시그니처만 유지하고 본문을 비웠다.
 */
@Composable
fun HiFiStatusBar(
    modifier: Modifier = Modifier,
    darkText: Boolean = true,
) {
    // no-op
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
 * (프로토타입 잔재) 가짜 하단 제스처 막대였음 — 실기기 OS 제스처바를 쓰고 루트 인셋으로
 * 처리되므로 아무것도 그리지 않는다. 시그니처만 유지하고 본문을 비웠다.
 */
@Composable
fun HiFiGestureNav(
    modifier: Modifier = Modifier,
    light: Boolean = false,
) {
    // no-op
}
