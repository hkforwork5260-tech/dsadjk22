package com.jobalert.app.ui.screens.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jobalert.app.data.model.JobCategories
import com.jobalert.app.data.model.JobCategoryCodes
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 필터 풀스크린.
 * HiFi_Filter 대응. 직군(2열 그리드) + 기업규모/경력/지역/마감일 (가변 칩).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    onClose: () -> Unit,
    onApply: (FilterSelection) -> Unit,
) {
    // 현재 세션 필터로 미리 체크(기본은 관심직군·관심규모). 적용은 일회성이라 관심은 안 바뀐다.
    val sizeLabelByCode = mapOf(
        "large_corp" to "대기업", "public" to "공기업", "mid_corp" to "중견",
        "small" to "중소", "foreign" to "외국계", "startup" to "스타트업", "startup_unicorn" to "스타트업",
    )
    var jobs by remember {
        mutableStateOf(ActiveFilter.categories.mapNotNull { c -> JobCategoryCodes.indexOf(c).takeIf { it >= 0 } }.toSet())
    }
    var sizes by remember { mutableStateOf(ActiveFilter.sizes.mapNotNull { sizeLabelByCode[it] }.toSet()) }
    var experience by remember {
        mutableStateOf(
            when {
                "신입" in ActiveFilter.experiences -> "신입"
                "경력" in ActiveFilter.experiences -> "1~3년"
                else -> ""
            },
        )
    }
    var locations by remember { mutableStateOf(emptySet<String>()) }
    var deadlines by remember {
        mutableStateOf(
            when (ActiveFilter.deadlineDays) {
                0 -> setOf("오늘"); 1 -> setOf("내일"); 3 -> setOf("D-3")
                7 -> setOf("D-7"); 14 -> setOf("D-14"); else -> emptySet()
            },
        )
    }

    val sizesAll = listOf("대기업", "공기업", "중견", "중소", "외국계", "스타트업")
    val expAll = listOf("신입", "1~3년", "3~5년", "5년+", "무관")
    val locAll = listOf("서울", "경기/인천", "대전", "부산", "광주", "대구", "+")
    val ddayAll = listOf("오늘", "내일", "D-3", "D-7", "D-14")

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "필터",
            leading = { HiFiIconBtn(Icons.Outlined.Close, "닫기", onClick = onClose) },
            action = {
                HiFiButton(
                    text = "초기화",
                    onClick = {
                        jobs = emptySet(); sizes = emptySet(); experience = ""
                        locations = emptySet(); deadlines = emptySet()
                    },
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp, bottom = 20.dp),
        ) {
            SectionHeader("직군", sub = "${jobs.size}개 선택")
            Spacer(Modifier.height(8.dp))
            CategoriesGrid(
                items = JobCategories,
                selected = jobs,
                onToggle = { i -> jobs = if (i in jobs) jobs - i else jobs + i },
            )

            Spacer(Modifier.height(22.dp))
            SectionHeader("기업 규모")
            Spacer(Modifier.height(8.dp))
            ChipFlow(sizesAll, sizes) { v ->
                sizes = if (v in sizes) sizes - v else sizes + v
            }

            Spacer(Modifier.height(22.dp))
            SectionHeader("경력")
            Spacer(Modifier.height(8.dp))
            ChipFlow(expAll, if (experience.isBlank()) emptySet() else setOf(experience)) { v ->
                experience = if (experience == v) "" else v
            }

            Spacer(Modifier.height(22.dp))
            SectionHeader("지역")
            Spacer(Modifier.height(8.dp))
            ChipFlow(locAll, locations) { v ->
                locations = if (v in locations) locations - v else locations + v
            }

            Spacer(Modifier.height(22.dp))
            SectionHeader("마감일")
            Spacer(Modifier.height(8.dp))
            ChipFlow(ddayAll, deadlines) { v ->
                deadlines = if (v in deadlines) deadlines - v else deadlines + v
            }
        }

        Column(Modifier.fillMaxWidth().background(HiFiColors.Bg)) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
            Box(Modifier.padding(16.dp)) {
                HiFiButton(
                    text = "이 조건으로 보기",
                    onClick = {
                        onApply(FilterSelection(jobs, sizes, experience, locations, deadlines))
                    },
                    variant = HiFiButtonVariant.Primary,
                    size = HiFiButtonSize.Lg,
                    fullWidth = true,
                )
            }
        }
        HiFiGestureNav()
    }
}

data class FilterSelection(
    val jobs: Set<Int>,
    val sizes: Set<String>,
    val experience: String,
    val locations: Set<String>,
    val deadlines: Set<String>,
)

@Composable
private fun SectionHeader(title: String, sub: String? = null) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(title, style = HiFiType.h2, color = HiFiColors.Text, modifier = Modifier.weight(1f))
        if (sub != null) {
            Text(sub, style = HiFiType.body2, color = HiFiColors.Text2)
        }
    }
}

@Composable
private fun CategoriesGrid(
    items: List<String>,
    selected: Set<Int>,
    onToggle: (Int) -> Unit,
) {
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEachIndexed { rIdx, row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                row.forEachIndexed { cIdx, label ->
                    val idx = rIdx * 2 + cIdx
                    HiFiButton(
                        text = label,
                        onClick = { onToggle(idx) },
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
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChipFlow(
    items: List<String>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items.forEach { v ->
            HiFiChip(
                text = v,
                selected = v in selected,
                variant = HiFiChipVariant.Outline,
                onClick = { onToggle(v) },
            )
        }
    }
}
