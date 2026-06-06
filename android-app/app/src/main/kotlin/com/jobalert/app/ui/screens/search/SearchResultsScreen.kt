package com.jobalert.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.JobsSearchResponse
import com.jobalert.app.data.model.JobCategories
import com.jobalert.app.data.model.JobCategoryCodes
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType
import com.jobalert.app.ui.theme.JobKind

/**
 * 검색 결과.
 * HiFi_SearchResults 대응. 전체/기업/공고 토글 + 기업 리스트 + 공고 카드 리스트.
 */
@Composable
fun SearchResultsScreen(
    query: String,
    categoryCode: String = "",
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
    onCompanyClick: (Int) -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    val isCategory = categoryCode.isNotBlank()
    // 직군 둘러보기면 직군 라벨을, 일반 검색이면 검색어를 헤더에 표시(빈 검색은 "삼성" 데모).
    val headerText = if (isCategory) categoryLabelOf(categoryCode) else query.ifBlank { "삼성" }
    // 백엔드 /jobs/search 연결(직군이면 categories 필터). 로딩·에러 시엔 빈 결과로 렌더.
    val viewModel: SearchViewModel = viewModel()
    LaunchedEffect(query, categoryCode) {
        if (isCategory) viewModel.search("", categoryCode) else viewModel.search(query.ifBlank { "삼성" })
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val response = (state as? SearchUiState.Success)?.response
        ?: JobsSearchResponse(query = headerText, totalEstimate = 0, jobs = emptyList())
    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        // 검색어 칩 (편집 가능 느낌)
        Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp).fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = HiFiColors.Text, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    headerText,
                    style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                    color = HiFiColors.Text,
                    modifier = Modifier.weight(1f),
                )
                Box(Modifier.clickable { onBack() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Close, contentDescription = "검색어 지우기", tint = HiFiColors.Text2, modifier = Modifier.size(14.dp))
                }
            }
        }

        // 구분선
        Box(Modifier.fillMaxWidth().padding(top = 10.dp).height(1.dp).background(HiFiColors.Border))

        // 결과 본문
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp),
        ) {
            if (response.jobs.isNotEmpty()) {
                Text("공고 (${response.jobs.size})", style = HiFiType.caption, color = HiFiColors.Text2)
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    response.jobs.forEach { job ->
                        HiFiJobCard(
                            kind = kindOf(job.kind),
                            company = job.company.name,
                            role = job.title,
                            logo = job.company.logo,
                            dday = job.dday,
                            dateText = "~${displayDeadline(job.deadline)}",
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            } else {
                Spacer(Modifier.height(20.dp))
                Text("일치하는 공고가 없어요", style = HiFiType.body2, color = HiFiColors.Text3)
            }
        }

        HiFiTabBar(active = HomeTab.Search, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

/** 직군 코드(it_dev_data) → 한글 라벨. 매핑 없으면 코드 그대로. */
private fun categoryLabelOf(code: String): String {
    val i = JobCategoryCodes.indexOf(code)
    return if (i in JobCategories.indices) JobCategories[i] else code
}

private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    "UPDATE" -> JobKind.UPDATE
    "CLOSING" -> JobKind.CLOSING
    else -> JobKind.NEW
}

/** ISO8601 마감 → "6/15" 같은 짧은 표기 */
private fun displayDeadline(iso: String?): String {
    // 단순 파싱: "2026-06-15T14:59:59Z" → "6/15"
    if (iso.isNullOrBlank()) return ""   // 상시채용(마감일 없음) 대응
    val date = iso.substringBefore('T')
    val parts = date.split('-')
    if (parts.size < 3) return ""
    val m = parts[1].trimStart('0')
    val d = parts[2].trimStart('0')
    return "$m/$d"
}

