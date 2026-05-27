package com.jobalert.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
    // 초기값: 디자인 프로토타입과 동일하게 IT개발·데이터, 디자인, 마케팅 셋 선택
    var selected by remember { mutableStateOf(setOf(2, 5, 6)) }
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

            // 21개 카테고리 2열 그리드
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(JobCategories) { category ->
                    val idx = JobCategories.indexOf(category)
                    HiFiButton(
                        text = category,
                        onClick = {
                            selected = if (idx in selected) selected - idx else selected + idx
                        },
                        variant = if (idx in selected) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        fullWidth = true,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            HiFiButton(
                text = "다음 ($count 개 선택됨) →",
                onClick = onNext,
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
