package com.jobalert.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 온보딩 ② 기업 규모 + 산업군 multi-select.
 * HiFi_Onb2 대응.
 */
@Composable
fun OnboardingCompanySizeScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
) {
    val scales = listOf("대기업", "공기업", "중견기업", "중소기업", "외국계", "스타트업")
    val sectors = listOf("IT/플랫폼", "반도체", "금융", "자동차", "바이오", "화학/소재", "유통", "+ 더보기")
    var selectedScales by remember { mutableStateOf(setOf(0, 1)) }
    var selectedSectors by remember { mutableStateOf(setOf(0, 1)) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 14.dp),
        ) {
            // 진행 dot + 건너뛰기
            Row(verticalAlignment = Alignment.CenterVertically) {
                OnboardingDots(total = 4, activeIndex = 1)
                Spacer(Modifier.weight(1f))
                HiFiButton(
                    text = "건너뛰기",
                    onClick = onSkip,
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 56.dp, expression = MascotExpression.Default)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("어떤 회사들이 궁금해?", style = HiFiType.display.copy(fontSize = 24.sp), color = HiFiColors.Text)
                    Text("매칭 정확도 ↑", style = HiFiType.body2, color = HiFiColors.Text2)
                }
            }

            // 본문 스크롤
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(top = 18.dp),
            ) {
                SectionLabel("기업 규모")
                Spacer(Modifier.height(8.dp))
                GridButtons(
                    items = scales,
                    selected = selectedScales,
                    columns = 3,
                    onToggle = { i ->
                        selectedScales = if (i in selectedScales) selectedScales - i else selectedScales + i
                    },
                )

                Spacer(Modifier.height(22.dp))
                SectionLabel("산업군")
                Spacer(Modifier.height(8.dp))
                GridButtons(
                    items = sectors,
                    selected = selectedSectors,
                    columns = 2,
                    onToggle = { i ->
                        selectedSectors = if (i in selectedSectors) selectedSectors - i else selectedSectors + i
                    },
                )
                Spacer(Modifier.height(20.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HiFiButton(
                    text = "← 이전",
                    onClick = onBack,
                    variant = HiFiButtonVariant.Default,
                    modifier = Modifier.weight(1f),
                    fullWidth = true,
                )
                HiFiButton(
                    text = "다음 →",
                    onClick = onNext,
                    variant = HiFiButtonVariant.Primary,
                    modifier = Modifier.weight(2f),
                    fullWidth = true,
                )
            }
        }
        HiFiGestureNav()
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = HiFiType.body2.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
        color = HiFiColors.Text2,
    )
}

@Composable
private fun GridButtons(
    items: List<String>,
    selected: Set<Int>,
    columns: Int,
    onToggle: (Int) -> Unit,
) {
    // 단순 수동 그리드 (스크롤 영역 안에서 LazyGrid 쓰면 nested scroll 충돌)
    val rows = items.chunked(columns)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEachIndexed { rIdx, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEachIndexed { cIdx, label ->
                    val globalIdx = rIdx * columns + cIdx
                    HiFiButton(
                        text = label,
                        onClick = { onToggle(globalIdx) },
                        variant = if (globalIdx in selected) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        fullWidth = true,
                        modifier = Modifier.weight(1f),
                    )
                }
                // 마지막 행이 빈 셀로 차지하지 않게 padding
                val missing = columns - row.size
                if (missing > 0) {
                    repeat(missing) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
