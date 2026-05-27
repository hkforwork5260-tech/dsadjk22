package com.jobalert.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.theme.*

/**
 * 좌측 로고(48dp) + 중앙 NEW라벨/회사명/직무명 + 우측 D-day/날짜.
 * README "JobCard" 스펙.
 */
@Composable
fun HiFiJobCard(
    kind: JobKind,
    company: String,
    role: String,
    logo: String,
    dday: String,
    dateText: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, HiFiColors.Border, RoundedCornerShape(16.dp))
            .background(HiFiColors.Bg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 로고
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(HiFiColors.Bg2),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                logo,
                style = HiFiType.body2.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold, fontSize = 14.sp),
                color = HiFiColors.Text,
            )
        }
        Spacer(Modifier.width(12.dp))

        // 중앙: NEW라벨 + 회사명 한 줄, 직무명 한 줄
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                HiFiLabel(text = kind.label(), bg = kind.color())
                Spacer(Modifier.width(6.dp))
                Text(
                    text = company,
                    style = HiFiType.body2.copy(fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = HiFiColors.Text2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = role,
                style = HiFiType.h2,
                color = HiFiColors.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.width(8.dp))

        // 우측: D-day + 날짜
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = dday,
                style = HiFiType.monoNum.copy(fontSize = 16.sp),
                color = kind.color(),
            )
            if (dateText.isNotBlank()) {
                Text(
                    text = dateText,
                    style = HiFiType.body2.copy(fontSize = 11.sp),
                    color = HiFiColors.Text2,
                )
            }
        }
    }
}
