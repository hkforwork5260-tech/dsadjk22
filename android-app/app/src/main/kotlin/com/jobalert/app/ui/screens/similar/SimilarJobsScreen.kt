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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType
import com.jobalert.app.ui.theme.JobKind

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
    // 백엔드 /jobs/{id} + /jobs/{id}/similar 연결.
    val viewModel: SimilarJobsViewModel = viewModel()
    LaunchedEffect(jobId) { viewModel.load(jobId) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val current = (state as? SimilarUiState.Success)?.current
        ?: Job(id = jobId, company = "", logo = "", role = "", kind = JobKind.NEW, dday = "", dateText = "")
    val others = (state as? SimilarUiState.Success)?.others ?: emptyList()

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

            if (others.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "비슷한 공고를 아직 못 찾았어요",
                    style = HiFiType.body2,
                    color = HiFiColors.Text2,
                )
            } else {
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
            }
        }

        HiFiGestureNav()
    }
}

