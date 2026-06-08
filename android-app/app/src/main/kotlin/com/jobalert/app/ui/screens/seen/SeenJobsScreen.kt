package com.jobalert.app.ui.screens.seen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.model.regionShort
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 본 공고 목록. 내정보 '본 공고' 숫자에서 진입.
 * 직접 자세히 본 공고들(SeenJobs)을 최근 본 순으로 보여준다. 비었으면 안내.
 */
@Composable
fun SeenJobsScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
) {
    val viewModel: SeenJobsViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val jobs = (state as? SeenJobsUiState.Success)?.jobs ?: emptyList()

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "본 공고",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            if (jobs.isNotEmpty()) {
                Text("본 공고 ${jobs.size}", style = HiFiType.caption, color = HiFiColors.Text2)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    jobs.forEach { job ->
                        HiFiJobCard(
                            kind = job.kind,
                            company = job.company,
                            role = job.role,
                            logo = job.regionShort,
                            dday = job.dday,
                            dateText = job.dateText,
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            } else {
                SeenEmpty()
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun SeenEmpty() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Mascot(size = 72.dp, expression = MascotExpression.Default)
        Spacer(Modifier.height(16.dp))
        Text(
            "아직 본 공고가 없어요",
            style = HiFiType.body,
            color = HiFiColors.Text,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "공고를 '자세히 보기'로 열면\n여기에 기록돼요",
            style = HiFiType.body2,
            color = HiFiColors.Text2,
            textAlign = TextAlign.Center,
        )
    }
}
