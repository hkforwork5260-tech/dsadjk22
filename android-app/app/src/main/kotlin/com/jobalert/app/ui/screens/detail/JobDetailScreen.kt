package com.jobalert.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*

private enum class DetailTab(val label: String) {
    Summary("요약"), Original("원문"), Company("회사"), Similar("비슷한")
}

@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onSimilarTab: () -> Unit,
    onApply: (Job) -> Unit,
) {
    // 백엔드 /jobs/{id} 연결. 로딩·에러 시엔 빈 공고로 렌더.
    val viewModel: JobDetailViewModel = viewModel()
    LaunchedEffect(jobId) { viewModel.load(jobId) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val job = (state as? JobDetailUiState.Success)?.job
        ?: Job(id = jobId, company = "", logo = "", role = "불러오는 중…", kind = JobKind.NEW, dday = "", dateText = "")
    var tab by remember { mutableStateOf(DetailTab.Summary) }
    var saved by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "",
            leading = {
                HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack)
            },
            action = {
                Row {
                    HiFiIconBtn(Icons.Outlined.BookmarkBorder, "저장", onClick = { saved = !saved })
                    Spacer(Modifier.width(8.dp))
                    HiFiIconBtn(Icons.Outlined.Share, "공유", onClick = onShare)
                }
            }
        )

        // 상단 정보 (로고 + 라벨 + 직무 + 회사)
        Column(Modifier.padding(horizontal = 20.dp).padding(bottom = 8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(HiFiColors.Bg2),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(job.logo, style = HiFiType.h2, color = HiFiColors.Text)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    HiFiLabel(text = job.kind.label(), bg = job.kind.color())
                    Spacer(Modifier.height(6.dp))
                    Text(job.role, style = HiFiType.title, color = HiFiColors.Text)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "${job.company} · ${job.location}",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            // 작은 칩 행
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                DDayChip(job.dday, job.kind)
                if (job.experience.isNotBlank()) HiFiChip(job.experience, small = true, variant = HiFiChipVariant.Outline)
                if (job.education.isNotBlank()) HiFiChip(job.education, small = true, variant = HiFiChipVariant.Outline)
                job.tags.firstOrNull()?.let { HiFiChip(it, small = true, variant = HiFiChipVariant.Outline) }
            }
        }

        // 탭바
        DetailTabRow(
            active = tab,
            onSelect = { selected ->
                if (selected == DetailTab.Similar) onSimilarTab() else tab = selected
            }
        )

        // 본문
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            when (tab) {
                DetailTab.Summary -> SummaryContent(job)
                DetailTab.Original -> Text(
                    "원문 보기: ${job.originalUrl.ifBlank { "(URL 미정)" }}",
                    style = HiFiType.body, color = HiFiColors.Text2,
                )
                DetailTab.Company -> Text(
                    "${job.company} 회사 정보. 회사 상세 화면으로 이동 가능.",
                    style = HiFiType.body, color = HiFiColors.Text2,
                )
                DetailTab.Similar -> Unit  // 도달하지 않음 (위 onSimilarTab으로 라우트)
            }
        }

        // 하단 지원하기
        Box(
            Modifier
                .fillMaxWidth()
                .border(1.dp, HiFiColors.Border)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            HiFiButton(
                text = "지원하러 가기",
                onClick = { onApply(job) },
                variant = HiFiButtonVariant.Primary,
                size = HiFiButtonSize.Lg,
                fullWidth = true,
            )
        }

        HiFiGestureNav()
    }
}

@Composable
private fun DDayChip(dday: String, kind: JobKind) {
    Box(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(kind.softColor())
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(dday, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = kind.shadowColor())
    }
}

@Composable
private fun DetailTabRow(active: DetailTab, onSelect: (DetailTab) -> Unit) {
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DetailTab.values().forEach { t ->
                val on = t == active
                Column(
                    Modifier
                        .clickable { onSelect(t) }
                        .padding(vertical = 10.dp),
                ) {
                    Text(
                        t.label,
                        style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                        color = if (on) HiFiColors.Brand else HiFiColors.Text3,
                    )
                    Spacer(Modifier.height(8.dp))
                    Box(
                        Modifier
                            .height(3.dp)
                            .width(28.dp)
                            .background(if (on) HiFiColors.Brand else Color.Transparent)
                    )
                }
            }
        }
        // 하단 구분선
        Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
    }
}

@Composable
private fun SummaryContent(job: Job) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(HiFiColors.BrandSoft)
            .border(2.dp, HiFiColors.Brand, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Text("✨", style = HiFiType.h2.copy(fontSize = 22.sp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("꽁이의 한줄 요약", style = HiFiType.caption, color = HiFiColors.BrandDark)
                Spacer(Modifier.height(4.dp))
                Text(
                    job.summary.ifBlank { "이 공고에 대한 AI 요약이 곧 생성됩니다." },
                    style = HiFiType.h2, color = HiFiColors.Text,
                )
            }
        }
    }
    Spacer(Modifier.height(22.dp))

    Text("📋 핵심 정보", style = HiFiType.h2, color = HiFiColors.Text)
    Spacer(Modifier.height(10.dp))
    val info = listOf(
        "마감" to "${job.dateText.removePrefix("~")} 23:59",
        "자격" to listOfNotNull(
            job.education.takeIf { it.isNotBlank() },
            job.experience.takeIf { it.isNotBlank() },
        ).joinToString(", "),
        "근무지" to job.location,
        "전형" to "서류 → 코딩 → 면접",
    )
    info.forEach { (k, v) ->
        Row(Modifier.padding(vertical = 5.dp)) {
            Text(k, style = HiFiType.body2, modifier = Modifier.width(60.dp))
            Text(
                if (v.isBlank()) "-" else v,
                style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                color = HiFiColors.Text,
                modifier = Modifier.weight(1f),
            )
        }
    }

    Spacer(Modifier.height(22.dp))
    Text("🎯 우대사항", style = HiFiType.h2, color = HiFiColors.Text)
    Spacer(Modifier.height(10.dp))
    listOf(
        "대규모 분산 시스템 경험",
        "Spring Boot / Kotlin 능숙자",
        "오픈소스 기여 경험",
    ).forEach {
        Text("• $it", style = HiFiType.body, color = HiFiColors.Text, modifier = Modifier.padding(vertical = 3.dp))
    }
}
