package com.jobalert.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.SeenJobs
import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*

private enum class DetailTab(val label: String) {
    Info("정보"), Similar("비슷한")
}

@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    onShare: () -> Unit,
    onSimilarTab: () -> Unit,
    onCompanyClick: (Int) -> Unit,
    onApply: (Job) -> Unit,
) {
    // 백엔드 /jobs/{id} 연결. 로딩·에러 시엔 빈 공고로 렌더.
    val viewModel: JobDetailViewModel = viewModel()
    // 상세화면 진입 = '직접 본 공고'로 기록(내 정보 '본 공고' 카운트·찾아보기 후순위에 반영).
    LaunchedEffect(jobId) { viewModel.load(jobId); SeenJobs.markSeen(jobId) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val job = (state as? JobDetailUiState.Success)?.job
        ?: Job(id = jobId, company = "", logo = "", role = "불러오는 중…", kind = JobKind.NEW, dday = "", dateText = "")
    val context = LocalContext.current
    var tab by remember { mutableStateOf(DetailTab.Info) }
    var saved by remember(jobId) { mutableStateOf(false) }
    var favorited by remember(jobId) { mutableStateOf(false) }
    // 백엔드가 알려준 저장·관심기업 상태로 초기 동기화(로드 완료 시).
    LaunchedEffect(job.isSaved) { saved = job.isSaved }
    LaunchedEffect(job.isFavoriteCompany) { favorited = job.isFavoriteCompany }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "",
            leading = {
                HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack)
            },
            action = {
                Row {
                    HiFiIconBtn(
                        if (favorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        "관심기업",
                        onClick = {
                            val cid = job.companyId
                            if (cid != null) {
                                val target = !favorited
                                favorited = target  // 낙관적 토글
                                viewModel.setFavorite(cid, target) { ok ->
                                    if (!ok) {
                                        favorited = !target  // 실패 롤백
                                        Toast.makeText(context, "관심기업 저장에 실패했어요. 잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                    )
                    Spacer(Modifier.width(8.dp))
                    HiFiIconBtn(
                        if (saved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        "저장",
                        onClick = {
                            val target = !saved
                            saved = target  // 낙관적 토글
                            viewModel.setSaved(jobId, target) { ok ->
                                if (!ok) {
                                    saved = !target  // 실패 시 롤백
                                    Toast.makeText(context, "저장에 실패했어요. 잠시 후 다시 시도해 주세요", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                    )
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
                companySizeLabel(job.companySize)?.let { HiFiChip(it, small = true, variant = HiFiChipVariant.Outline) }
                job.categories.firstOrNull()?.let { HiFiChip(it, small = true, variant = HiFiChipVariant.Outline) }
                if (job.education.isNotBlank()) HiFiChip(job.education, small = true, variant = HiFiChipVariant.Outline)
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
                DetailTab.Info -> InfoContent(
                    job = job,
                    onOpenOriginal = { onApply(job) },
                    onOpenCompany = { job.companyId?.let(onCompanyClick) },
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

/**
 * 정보 탭 — 핵심정보(수집한 메타데이터) + 원문 링크 + 회사 링크.
 * (AI 한줄요약·우대사항은 데이터 미수집이라 제거 — 실제 있는 정보만 보여줌)
 */
@Composable
private fun InfoContent(job: Job, onOpenOriginal: () -> Unit, onOpenCompany: () -> Unit) {
    Text("📋 핵심 정보", style = HiFiType.h2, color = HiFiColors.Text)
    Spacer(Modifier.height(10.dp))
    val info = listOf(
        "마감" to job.dateText.removePrefix("~").ifBlank { "상시" },
        "급여" to job.salary,
        "직군" to job.categories.joinToString(", "),
        "경력" to job.experience,
        "회사규모" to (companySizeLabel(job.companySize) ?: ""),
        "학력" to job.education,
        "근무지" to job.location,
        "태그" to job.tags.joinToString(", "),
    )
    info.forEach { (k, v) ->
        if (v.isNotBlank()) {
            Row(Modifier.padding(vertical = 5.dp)) {
                Text(k, style = HiFiType.body2, color = HiFiColors.Text2, modifier = Modifier.width(60.dp))
                Text(
                    v,
                    style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                    color = HiFiColors.Text,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    // 공고 본문 (수집된 경우만 — Greenhouse 등 본문 제공 소스). 길어서 기본 접고 "더보기"로 펼침.
    if (job.description.isNotBlank()) {
        var descExpanded by remember(job.id) { mutableStateOf(false) }
        Spacer(Modifier.height(22.dp))
        Text("📄 상세 내용", style = HiFiType.h2, color = HiFiColors.Text)
        Spacer(Modifier.height(10.dp))
        Text(
            job.description,
            style = HiFiType.body.copy(lineHeight = 22.sp),
            color = HiFiColors.Text,
            maxLines = if (descExpanded) Int.MAX_VALUE else 8,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            if (descExpanded) "접기 ▴" else "더보기 ▾",
            style = HiFiType.body2.copy(fontWeight = FontWeight.Bold),
            color = HiFiColors.Brand,
            modifier = Modifier.clickable { descExpanded = !descExpanded },
        )
    }

    Spacer(Modifier.height(22.dp))
    LinkRow(label = "원문 공고 보기", onClick = onOpenOriginal)
    Spacer(Modifier.height(10.dp))
    LinkRow(label = "${job.company} 회사 정보", onClick = onOpenCompany)
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HiFiColors.Bg2)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text, modifier = Modifier.weight(1f))
        Text("↗", style = HiFiType.body, color = HiFiColors.Text2)
    }
}

/** 회사규모 코드 → 한글 라벨. 빈/모르는 값은 null(미노출). DB 실제값: large_corp·public·startup_unicorn. */
private fun companySizeLabel(code: String): String? = when (code) {
    "large_corp" -> "대기업"
    "mid_corp" -> "중견기업"
    "small" -> "중소기업"
    "public" -> "공기업"
    "startup", "startup_unicorn" -> "스타트업"
    "foreign" -> "외국계"
    else -> null
}
