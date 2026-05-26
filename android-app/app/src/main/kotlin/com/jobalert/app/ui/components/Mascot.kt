package com.jobalert.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class MascotExpression { Default, Happy, Sleep, Sad, Wow, Wave }

/**
 * 꽁이 (고양이) SVG-equivalent. 100×100 viewBox 기준 좌표를 Canvas 비율로 환산.
 * HTML 디자인의 shared.jsx Mascot 컴포넌트를 그대로 옮김.
 *
 * 프로덕션에서는 일러스트레이터에게 의뢰해서 정식 일러스트(VectorDrawable 또는 Lottie)로 교체 권장.
 */
@Composable
fun Mascot(
    size: Dp,
    expression: MascotExpression = MascotExpression.Default,
    modifier: Modifier = Modifier,
) {
    val fillColor = Color(0xFFFAD9B0)        // 고양이 베이지
    val earInner = Color(0xFFFFB8C2)
    val outline = Color(0xFF1A1A1A)
    val nosePink = Color(0xFFFF8FA3)
    val cheekPink = Color(0xFFFF8FA3)
    val tongue = Color(0xFFFF6B8A)

    Canvas(modifier = modifier.size(size)) {
        val s = this.size.minDimension
        fun p(x: Float, y: Float) = Offset(x / 100f * s, y / 100f * s)
        fun len(v: Float) = v / 100f * s
        val strokeW = len(2.4f)
        val strokeWThin = len(1.4f)
        val strokeWMid = len(2.0f)

        // 바닥 그림자
        drawOval(
            color = Color.Black.copy(alpha = 0.08f),
            topLeft = p(28f, 90.5f),
            size = Size(len(44f), len(5f)),
        )

        // 두 발
        drawOval(color = fillColor, topLeft = p(34f, 85.5f), size = Size(len(12f), len(7f)))
        drawOval(color = outline, topLeft = p(34f, 85.5f), size = Size(len(12f), len(7f)), style = Stroke(strokeWMid))
        drawOval(color = fillColor, topLeft = p(54f, 85.5f), size = Size(len(12f), len(7f)))
        drawOval(color = outline, topLeft = p(54f, 85.5f), size = Size(len(12f), len(7f)), style = Stroke(strokeWMid))

        // 귀 (삼각형)
        val leftEar = Path().apply {
            moveTo(p(26f, 30f).x, p(26f, 30f).y)
            lineTo(p(22f, 12f).x, p(22f, 12f).y)
            lineTo(p(38f, 22f).x, p(38f, 22f).y)
            close()
        }
        val rightEar = Path().apply {
            moveTo(p(74f, 30f).x, p(74f, 30f).y)
            lineTo(p(78f, 12f).x, p(78f, 12f).y)
            lineTo(p(62f, 22f).x, p(62f, 22f).y)
            close()
        }
        drawPath(leftEar, fillColor)
        drawPath(leftEar, outline, style = Stroke(len(2.2f)))
        drawPath(rightEar, fillColor)
        drawPath(rightEar, outline, style = Stroke(len(2.2f)))

        val leftEarInner = Path().apply {
            moveTo(p(27f, 26f).x, p(27f, 26f).y)
            lineTo(p(26f, 18f).x, p(26f, 18f).y)
            lineTo(p(32f, 22f).x, p(32f, 22f).y)
            close()
        }
        val rightEarInner = Path().apply {
            moveTo(p(73f, 26f).x, p(73f, 26f).y)
            lineTo(p(74f, 18f).x, p(74f, 18f).y)
            lineTo(p(68f, 22f).x, p(68f, 22f).y)
            close()
        }
        drawPath(leftEarInner, earInner)
        drawPath(rightEarInner, earInner)

        // 본체 (몽글한 동그라미)
        val body = Path().apply {
            moveTo(p(50f, 22f).x, p(50f, 22f).y)
            cubicTo(p(28f, 22f).x, p(28f, 22f).y, p(20f, 38f).x, p(20f, 38f).y, p(20f, 56f).x, p(20f, 56f).y)
            cubicTo(p(20f, 78f).x, p(20f, 78f).y, p(32f, 88f).x, p(32f, 88f).y, p(50f, 88f).x, p(50f, 88f).y)
            cubicTo(p(68f, 88f).x, p(68f, 88f).y, p(80f, 78f).x, p(80f, 78f).y, p(80f, 56f).x, p(80f, 56f).y)
            cubicTo(p(80f, 38f).x, p(80f, 38f).y, p(72f, 22f).x, p(72f, 22f).y, p(50f, 22f).x, p(50f, 22f).y)
            close()
        }
        drawPath(body, fillColor)
        drawPath(body, outline, style = Stroke(strokeW))

        // 눈 (표정별)
        when (expression) {
            MascotExpression.Default, MascotExpression.Wave -> {
                drawOval(color = outline, topLeft = p(33.8f, 47f), size = Size(len(8.4f), len(10f)))
                drawOval(color = outline, topLeft = p(57.8f, 47f), size = Size(len(8.4f), len(10f)))
                drawCircle(color = Color.White, radius = len(1.4f), center = p(39.5f, 50f))
                drawCircle(color = Color.White, radius = len(1.4f), center = p(63.5f, 50f))
            }
            MascotExpression.Happy -> {
                val l = Path().apply {
                    moveTo(p(34f, 53f).x, p(34f, 53f).y)
                    quadraticBezierTo(p(38f, 48f).x, p(38f, 48f).y, p(42f, 53f).x, p(42f, 53f).y)
                }
                val r = Path().apply {
                    moveTo(p(58f, 53f).x, p(58f, 53f).y)
                    quadraticBezierTo(p(62f, 48f).x, p(62f, 48f).y, p(66f, 53f).x, p(66f, 53f).y)
                }
                drawPath(l, outline, style = Stroke(len(2.8f)))
                drawPath(r, outline, style = Stroke(len(2.8f)))
            }
            MascotExpression.Sleep -> {
                drawLine(outline, p(33f, 53f), p(43f, 53f), len(2.6f))
                drawLine(outline, p(57f, 53f), p(67f, 53f), len(2.6f))
            }
            MascotExpression.Sad -> {
                drawOval(color = outline, topLeft = p(34f, 49.5f), size = Size(len(8f), len(9f)))
                drawOval(color = outline, topLeft = p(58f, 49.5f), size = Size(len(8f), len(9f)))
            }
            MascotExpression.Wow -> {
                drawOval(color = outline, topLeft = p(33f, 46f), size = Size(len(10f), len(12f)))
                drawOval(color = outline, topLeft = p(57f, 46f), size = Size(len(10f), len(12f)))
                drawCircle(color = Color.White, radius = len(1.6f), center = p(39.5f, 50f))
                drawCircle(color = Color.White, radius = len(1.6f), center = p(63.5f, 50f))
            }
        }

        // 코 (분홍 삼각형)
        val nose = Path().apply {
            moveTo(p(47f, 60f).x, p(47f, 60f).y)
            lineTo(p(53f, 60f).x, p(53f, 60f).y)
            lineTo(p(50f, 64f).x, p(50f, 64f).y)
            close()
        }
        drawPath(nose, nosePink)
        drawPath(nose, outline, style = Stroke(strokeWThin))

        // 입 + 수염
        val mouthLeft = Path().apply {
            moveTo(p(50f, 64f).x, p(50f, 64f).y)
            quadraticBezierTo(p(50f, 68f).x, p(50f, 68f).y, p(46f, 68f).x, p(46f, 68f).y)
        }
        val mouthRight = Path().apply {
            moveTo(p(50f, 64f).x, p(50f, 64f).y)
            quadraticBezierTo(p(50f, 68f).x, p(50f, 68f).y, p(54f, 68f).x, p(54f, 68f).y)
        }
        drawPath(mouthLeft, outline, style = Stroke(strokeWMid))
        drawPath(mouthRight, outline, style = Stroke(strokeWMid))

        // 수염 (좌·우 각 2개)
        drawLine(outline.copy(alpha = 0.7f), p(25f, 64f), p(38f, 62f), strokeWThin)
        drawLine(outline.copy(alpha = 0.7f), p(25f, 68f), p(38f, 67f), strokeWThin)
        drawLine(outline.copy(alpha = 0.7f), p(75f, 64f), p(62f, 62f), strokeWThin)
        drawLine(outline.copy(alpha = 0.7f), p(75f, 68f), p(62f, 67f), strokeWThin)

        // 볼터치
        drawCircle(color = cheekPink.copy(alpha = 0.55f), radius = len(4f), center = p(28f, 68f))
        drawCircle(color = cheekPink.copy(alpha = 0.55f), radius = len(4f), center = p(72f, 68f))

        // Wave일 때 팔 한쪽
        if (expression == MascotExpression.Wave) {
            drawOval(
                color = fillColor,
                topLeft = p(78f, 56f),
                size = Size(len(10f), len(7f)),
            )
            drawOval(
                color = outline,
                topLeft = p(78f, 56f),
                size = Size(len(10f), len(7f)),
                style = Stroke(strokeWMid),
            )
        }
    }
}
