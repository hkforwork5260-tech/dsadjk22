package com.jobalert.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * HiFi 디자인 토큰. README "컬러 토큰" 표 그대로.
 */
object HiFiColors {
    // Brand (Coral)
    val Brand = Color(0xFFFF6B35)
    val BrandHover = Color(0xFFFF5722)
    val BrandShadow = Color(0xFFD9532A)
    val BrandDark = Color(0xFFE5522A)
    val BrandSoft = Color(0xFFFFF0E8)

    // NEW (Green - Duolingo)
    val New = Color(0xFF58CC02)
    val NewShadow = Color(0xFF4AB801)
    val NewSoft = Color(0xFFE7F9D6)

    // UPDATE (Yellow)
    val Update = Color(0xFFFFC800)
    val UpdateShadow = Color(0xFFE0A800)
    val UpdateSoft = Color(0xFFFFF7D6)

    // CLOSING (Red)
    val Closing = Color(0xFFFF4B4B)
    val ClosingShadow = Color(0xFFE63D3D)
    val ClosingSoft = Color(0xFFFFE1E1)

    // INFO (Blue)
    val Info = Color(0xFF1CB0F6)
    val InfoSoft = Color(0xFFE1F3FC)

    // Text
    val Text = Color(0xFF3C3C3C)
    val Text2 = Color(0xFF777777)
    val Text3 = Color(0xFFAFAFAF)

    // Background
    val Bg = Color(0xFFFFFFFF)
    val Bg2 = Color(0xFFF7F8FA)
    val Bg3 = Color(0xFFEFEFF1)

    // Border
    val Border = Color(0xFFE5E5E5)
    val BorderDark = Color(0xFFD1D1D1)
}

// ACTIVE = 일반 진행중(NEW/UPDATE/CLOSING 아님). 메인 토글엔 안 뜨고 찾아보기·검색에 노출.
enum class JobKind { NEW, UPDATE, CLOSING, ACTIVE }

fun JobKind.color(): Color = when (this) {
    JobKind.NEW -> HiFiColors.New
    JobKind.UPDATE -> HiFiColors.Update
    JobKind.CLOSING -> HiFiColors.Closing
    JobKind.ACTIVE -> HiFiColors.Text2
}

fun JobKind.softColor(): Color = when (this) {
    JobKind.NEW -> HiFiColors.NewSoft
    JobKind.UPDATE -> HiFiColors.UpdateSoft
    JobKind.CLOSING -> HiFiColors.ClosingSoft
    JobKind.ACTIVE -> HiFiColors.Bg2
}

fun JobKind.shadowColor(): Color = when (this) {
    JobKind.NEW -> HiFiColors.NewShadow
    JobKind.UPDATE -> HiFiColors.UpdateShadow
    JobKind.CLOSING -> HiFiColors.ClosingShadow
    JobKind.ACTIVE -> HiFiColors.Text3
}

fun JobKind.label(): String = when (this) {
    JobKind.NEW -> "NEW"
    JobKind.UPDATE -> "UPDATE"
    JobKind.CLOSING -> "CLOSING"
    JobKind.ACTIVE -> "진행중"
}
