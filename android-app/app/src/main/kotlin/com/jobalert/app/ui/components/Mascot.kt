package com.jobalert.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.jobalert.app.R

enum class MascotExpression { Default, Happy, Sleep, Sad, Wow, Wave }

/**
 * 표정 → 마스코트 '단이'(시바견) PNG 리소스.
 * 2026-06-08 리브랜딩: 기존 고양이 '단이' Canvas 드로잉을 시바 PNG(mascot-dan 번들)로 교체.
 * 전용 sad 자산이 없어 Sad는 alert(깜짝)로 근접 매핑.
 */
fun MascotExpression.drawableRes(): Int = when (this) {
    MascotExpression.Happy -> R.drawable.mascot_happy
    MascotExpression.Default -> R.drawable.mascot_calm
    MascotExpression.Wave -> R.drawable.mascot_wink
    MascotExpression.Wow -> R.drawable.mascot_excited
    MascotExpression.Sad -> R.drawable.mascot_alert
    MascotExpression.Sleep -> R.drawable.mascot_sleepy
}

/**
 * 마스코트 단이. 투명배경 PNG를 [size] 정사각으로 표시.
 * 위젯(RemoteViews)은 [com.jobalert.app.widget.MascotRenderer]가 같은 매핑으로 Bitmap 렌더.
 */
@Composable
fun Mascot(
    size: Dp,
    expression: MascotExpression = MascotExpression.Default,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(expression.drawableRes()),
        contentDescription = null,
        modifier = modifier.size(size),
    )
}
