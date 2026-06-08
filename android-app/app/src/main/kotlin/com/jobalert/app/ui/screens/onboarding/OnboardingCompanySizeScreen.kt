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
import com.jobalert.app.ui.screens.filter.ActiveFilter
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
    // 실제 수집 데이터에 있는 규모만(중견·외국계·스타트업은 데이터 0). 인덱스 = scaleCodes와 1:1.
    val scales = listOf("대기업", "공기업", "중소기업")
    val scaleCodes = listOf("large_corp", "public", "small")
    // 산업군 — 사람인 기준 주요 분야 + 기타. 더보기 칸 없이 한눈에 다 보임.
    val sectors = listOf(
        "IT/플랫폼", "반도체", "금융",
        "자동차", "바이오/제약", "화학/소재",
        "유통/식품", "게임", "미디어/엔터",
        "통신/방송", "에너지/중공업", "건설/건축",
        "항공/물류", "교육/공공", "패션/뷰티",
        "기타",
    )
    // 처음엔 빈 상태(직접 고름). 내정보 '규모 수정' 재진입 시 저장된 값(ActiveFilter) 반영.
    var selectedScales by remember {
        mutableStateOf(ActiveFilter.interestSizes.mapNotNull { code -> scaleCodes.indexOf(code).takeIf { it >= 0 } }.toSet())
    }
    var selectedSectors by remember { mutableStateOf(emptySet<Int>()) }

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
                // '어디든' = 규모 상관없이 전체. 선택 시 규모 선택 해제(빈 선택 = 전체 필터). 규모를 하나 고르면 자동 해제.
                AnyOption(
                    active = selectedScales.isEmpty(),
                    onClick = { selectedScales = emptySet() },
                )
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
                AnyOption(
                    active = selectedSectors.isEmpty(),
                    onClick = { selectedSectors = emptySet() },
                )
                Spacer(Modifier.height(8.dp))
                GridButtons(
                    items = sectors,
                    selected = selectedSectors,
                    columns = 3,
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
                    onClick = {
                        // 선택한 규모를 백엔드 코드로 변환해 저장 → 메인 피드 필터에 반영.
                        ActiveFilter.setInterest(sizes = selectedScales.sorted().mapNotNull { scaleCodes.getOrNull(it) })
                        onNext()
                    },
                    variant = HiFiButtonVariant.Primary,
                    modifier = Modifier.weight(2f),
                    fullWidth = true,
                )
            }
        }
        HiFiGestureNav()
    }
}

/** '어디든 취업시켜주세요' 옵션 — 해당 분류를 신경 안 쓰고 전체를 보겠다는 칸. */
@Composable
private fun AnyOption(active: Boolean, onClick: () -> Unit) {
    HiFiButton(
        text = "😢 어디든 취업시켜주세요",
        onClick = onClick,
        variant = if (active) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
        size = HiFiButtonSize.Sm,
        fullWidth = true,
        maxLines = 1,
    )
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
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                row.forEachIndexed { cIdx, label ->
                    val globalIdx = rIdx * columns + cIdx
                    HiFiButton(
                        text = label,
                        onClick = { onToggle(globalIdx) },
                        variant = if (globalIdx in selected) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        fullWidth = true,
                        maxLines = 1,
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
