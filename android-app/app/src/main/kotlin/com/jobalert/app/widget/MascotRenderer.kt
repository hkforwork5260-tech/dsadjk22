package com.jobalert.app.widget

import android.graphics.Bitmap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import com.jobalert.app.ui.components.MascotExpression
import com.jobalert.app.ui.components.drawMascot

/**
 * 꽁이(Compose Canvas)를 위젯용 Bitmap으로 렌더. 위젯(RemoteViews)은 Canvas를 못 쓰므로
 * ImageView에 넣을 Bitmap이 필요하다. [drawMascot] 로직을 그대로 재사용한다.
 */
object MascotRenderer {
    fun render(expression: MascotExpression, sizePx: Int): Bitmap {
        val px = sizePx.coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap.asImageBitmap())
        CanvasDrawScope().draw(
            density = Density(1f),
            layoutDirection = LayoutDirection.Ltr,
            canvas = canvas,
            size = Size(px.toFloat(), px.toFloat()),
        ) {
            drawMascot(expression)
        }
        return bitmap
    }
}
