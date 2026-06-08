package com.jobalert.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.model.JobCategories
import com.jobalert.app.data.model.JobCategoryCodes
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 온보딩 ① 직군 선택 (21개 카테고리 2열 그리드).
 * HiFi_Onb1 대응.
 */
@Composable
fun OnboardingJobCategoryScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    // 처음 온보딩이면 빈 상태(직접 고름). 내정보에서 '직군 수정'으로 재진입하면 저장된 값(ActiveFilter)을 반영.
    var selected by remember {
        mutableStateOf(ActiveFilter.interestCategories.mapNotNull { code -> JobCategoryCodes.indexOf(code).takeIf { it >= 0 } }.toSet())
    }
    val count = selected.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HiFiColors.Bg)
    ) {
        HiFiStatusBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 14.dp),
        ) {
            // 진행 dot + 건너뛰기
            Row(verticalAlignment = Alignment.CenterVertically) {
                Dot(active = true)
                Spacer(Modifier.width(6.dp))
                Dot()
                Spacer(Modifier.width(6.dp))
                Dot()
                Spacer(Modifier.width(6.dp))
                Dot()
                Spacer(Modifier.weight(1f))
                HiFiButton(
                    text = "건너뛰기",
                    onClick = onSkip,
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            }

            Spacer(Modifier.height(12.dp))

            // 마스코트 + 헤드라인
            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 56.dp, expression = MascotExpression.Wave)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "어떤 일을 찾고 있어?",
                        style = HiFiType.display.copy(fontSize = 24.sp),
                        color = HiFiColors.Text,
                    )
                    Text(
                        text = "복수 선택 OK",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // 21개 카테고리 2열 그리드 (수동, 일관성을 위해 다른 화면들과 동일 패턴)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                JobCategories.chunked(2).forEachIndexed { rIdx, row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        row.forEachIndexed { cIdx, category ->
                            val idx = rIdx * 2 + cIdx
                            HiFiButton(
                                text = category,
                                onClick = {
                                    selected = if (idx in selected) selected - idx else selected + idx
                                },
                                variant = if (idx in selected) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                                size = HiFiButtonSize.Sm,
                                fullWidth = true,
                                maxLines = 1,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (row.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            HiFiButton(
                text = "다음 ($count 개 선택됨) →",
                onClick = {
                    // 고른 관심 직군을 저장 → 메인 피드가 이 직군으로 기본 필터됨
                    ActiveFilter.setInterest(categories = selected.mapNotNull { JobCategoryCodes.getOrNull(it) })
                    onNext()
                },
                variant = if (count > 0) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                enabled = count > 0,
                fullWidth = true,
            )
        }

        HiFiGestureNav()
    }
}

@Composable
private fun Dot(active: Boolean = false) {
    val w = if (active) 24.dp else 8.dp
    Box(
        Modifier
            .height(8.dp)
            .width(w)
            .clip(if (active) RoundedCornerShape(4.dp) else CircleShape)
            .background(if (active) HiFiColors.Brand else HiFiColors.Bg3)
    )
}
