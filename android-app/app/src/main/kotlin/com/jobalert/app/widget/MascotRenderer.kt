package com.jobalert.app.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jobalert.app.ui.components.MascotExpression
import com.jobalert.app.ui.components.drawableRes

/**
 * 마스코트 '단이'(시바) PNG를 위젯용 Bitmap으로 렌더. 위젯(RemoteViews)은 Compose/Canvas를 못 쓰므로
 * drawable PNG를 디코드해 [sizePx] 정사각으로 스케일한다. 표정 매핑은 [drawableRes]를 그대로 공유.
 */
object MascotRenderer {
    fun render(context: Context, expression: MascotExpression, sizePx: Int): Bitmap {
        val px = sizePx.coerceAtLeast(1)
        val src = BitmapFactory.decodeResource(context.resources, expression.drawableRes())
        return Bitmap.createScaledBitmap(src, px, px, true)
    }
}
