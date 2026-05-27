package com.jobalert.app.ui.screens.similar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.model.Job
import com.jobalert.app.data.sample.SampleJobs
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType
import com.jobalert.app.ui.theme.color
import com.jobalert.app.ui.theme.label

/**
 * 비슷한 공고 리스트.
 * v0.1: SampleJobs에서 동일 회사·동일 직군 키워드 우선 정렬.
 * 백엔드 Phase 3에서 GET /v1/jobs/{id}/similar로 교체.
 */
@Composable
fun SimilarJobsScreen(
    jobId: String,
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
) {
    val current = remember(jobId) { SampleJobs.find { it.id == jobId } ?: SampleJobs[1] }
    val others = remember(jobId) {
        SampleJobs
            .filter { it.id != current.id }
            .sortedByDescending { similarityScore(current, it) }
    }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "비슷한 공고",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 16.dp),
        ) {
            // 현재 공고 안내 카드
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .border(1.dp, HiFiColors.Border, RoundedCornerShape(14.dp))
                    .padding(14.dp),
            ) {
                Column {
                    Text(
                        "이 공고와 비슷한",
                        style = HiFiType.body2.copy(fontSize = 12.sp),
                        color = HiFiColors.Text2,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${current.company} · ${current.role}",
                        style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                        color = HiFiColors.Text,
                        maxLines = 1,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Mascot(size = 36.dp, expression = MascotExpression.Wave)
                Spacer(Modifier.width(10.dp))
                Text(
                    "꽁이가 골라온 ${others.size}개",
                    style = HiFiType.h2,
                    color = HiFiColors.Text,
                )
            }
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                others.forEach { j ->
                    HiFiJobCard(
                        kind = j.kind,
                        company = j.company,
                        role = j.role,
                        logo = j.logo,
                        dday = j.dday,
                        dateText = j.dateText,
                        onClick = { onJobClick(j.id) },
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text(
                "* 공고 출처: 사람인",
                style = HiFiType.body2.copy(fontSize = 11.sp),
                color = HiFiColors.Text3,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        HiFiGestureNav()
    }
}

/**
 * 같은 회사면 +3, 같은 종류(NEW/UPDATE/CLOSING)면 +1, 태그 겹치면 각 +2.
 */
private fun similarityScore(a: Job, b: Job): Int {
    var score = 0
    if (a.company == b.company) score += 3
    if (a.kind == b.kind) score += 1
    val overlap = a.tags.toSet().intersect(b.tags.toSet()).size
    score += overlap * 2
    return score
}
