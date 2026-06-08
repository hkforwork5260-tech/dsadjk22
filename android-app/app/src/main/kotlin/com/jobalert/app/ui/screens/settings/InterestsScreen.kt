package com.jobalert.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.model.JobCategories
import com.jobalert.app.data.model.JobCategoryCodes
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 관심 직군 / 산업군 / 회사 규모 모아 보기.
 * v0.1: 현재 설정 표시 + 섹션별 "수정하기" → 온보딩 해당 단계로 이동.
 * 실제 영속 저장은 v0.2 (DataStore).
 */
@Composable
fun InterestsScreen(
    onBack: () -> Unit,
    onEditJobCategory: () -> Unit,
    onEditCompanySize: () -> Unit,
    onOpenFavorites: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "관심",
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
                Mascot(size = 48.dp, expression = MascotExpression.Wave)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "꽁이가 찾아줄 조건",
                        style = HiFiType.display.copy(fontSize = 20.sp),
                        color = HiFiColors.Text,
                    )
                    Text(
                        "조건을 바꾸면 다음 알림부터 반영돼요",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // 실제 관심 직군(ActiveFilter) 반영 — 온보딩/필터에서 고른 직군.
            val jobLabels = ActiveFilter.interestCategories.mapNotNull { code ->
                JobCategoryCodes.indexOf(code).takeIf { it >= 0 }?.let { JobCategories[it] }
            }
            SectionCard(
                title = "직군",
                count = if (jobLabels.isEmpty()) "전체" else "${jobLabels.size}개 선택",
                chips = jobLabels.ifEmpty { listOf("전체 직군") },
                chipColor = HiFiColors.Brand,
                chipSoft = HiFiColors.BrandSoft,
                onEdit = onEditJobCategory,
            )

            Spacer(Modifier.height(14.dp))

            // 회사 규모도 실제 선택값(ActiveFilter) 반영.
            val sizeLabels = ActiveFilter.interestSizes.mapNotNull { sizeLabelOf(it) }
            SectionCard(
                title = "회사 규모",
                count = if (sizeLabels.isEmpty()) "전체" else "${sizeLabels.size}개 선택",
                chips = sizeLabels.ifEmpty { listOf("전체") },
                chipColor = HiFiColors.Info,
                chipSoft = HiFiColors.InfoSoft,
                onEdit = onEditCompanySize,
            )

            // 관심 기업 섹션은 제거 — 하단 탭에 '관심기업'이 따로 있음(중복).

            Spacer(Modifier.height(22.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .padding(14.dp),
            ) {
                Text(
                    "💡 조건을 너무 좁히면 알림이 줄어들 수 있어요.\n" +
                        "처음엔 넓게 시작해서 점점 좁혀가는 걸 추천!",
                    style = HiFiType.body2.copy(fontSize = 13.sp),
                    color = HiFiColors.Text2,
                )
            }
        }

        HiFiGestureNav()
    }
}

/** 회사 규모 코드 → 한글 라벨. 모르는 값은 null. */
private fun sizeLabelOf(code: String): String? = when (code) {
    "large_corp" -> "대기업"
    "public" -> "공기업"
    "small" -> "중소기업"
    "mid_corp" -> "중견기업"
    "startup", "startup_unicorn" -> "스타트업"
    "foreign" -> "외국계"
    else -> null
}

@Composable
private fun SectionCard(
    title: String,
    count: String,
    chips: List<String>,
    chipColor: androidx.compose.ui.graphics.Color,
    chipSoft: androidx.compose.ui.graphics.Color,
    onEdit: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, HiFiColors.Border, RoundedCornerShape(16.dp))
            .clickable(onClick = onEdit)
            .padding(16.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(title, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text)
                    Text(count, style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
                }
                Icon(
                    Icons.Outlined.ChevronRight,
                    contentDescription = "수정",
                    tint = HiFiColors.Text3,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (chips.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                FlowChips(chips, chipColor, chipSoft)
            }
        }
    }
}

@Composable
private fun FlowChips(
    chips: List<String>,
    color: androidx.compose.ui.graphics.Color,
    soft: androidx.compose.ui.graphics.Color,
) {
    // 칸수 적어서 단순 Row + spacedBy. 넘치면 줄바꿈 안 됨 — 한 줄 짧게 유지.
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        chips.forEach { c ->
            Box(
                Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(soft)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(c, style = HiFiType.body2.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold), color = color)
            }
        }
    }
}
