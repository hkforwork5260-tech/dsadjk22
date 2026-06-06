package com.jobalert.app.ui.screens.saved

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
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 저장한 공고 목록. 마이페이지 "저장한 공고"에서 진입.
 * 공고 카드(HiFiJobCard) 리스트. 비었으면 안내 카드.
 */
@Composable
fun SavedJobsScreen(
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
) {
    val viewModel: SavedJobsViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val jobs = (state as? SavedJobsUiState.Success)?.jobs ?: emptyList()

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "저장한 공고",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            if (jobs.isNotEmpty()) {
                Text("저장한 공고 ${jobs.size}", style = HiFiType.caption, color = HiFiColors.Text2)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    jobs.forEach { job ->
                        HiFiJobCard(
                            kind = job.kind,
                            company = job.company,
                            role = job.role,
                            logo = job.logo,
                            dday = job.dday,
                            dateText = job.dateText,
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            } else {
                SavedEmpty()
            }
        }

        HiFiGestureNav()
    }
}

@Composable
private fun SavedEmpty() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Mascot(size = 72.dp, expression = MascotExpression.Default)
        Spacer(Modifier.height(16.dp))
        Text(
            "아직 저장한 공고가 없어요",
            style = HiFiType.body.copy(),
            color = HiFiColors.Text,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "공고 상세에서 🔖 북마크를 누르면\n여기에 모아둘 수 있어요",
            style = HiFiType.body2,
            color = HiFiColors.Text2,
            textAlign = TextAlign.Center,
        )
    }
}
