package com.jobalert.app.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.JobRepository
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.theme.*

/**
 * 메인 피드. 상단 헤더(오늘 새 공고 N건 + 꽁이) + NEW/UPDATE/CLOSING 토글 + 공고 리스트.
 * HiFi_Main 대응. 데이터는 [MainViewModel]을 통해 백엔드 /jobs/today에서 온다.
 */
@Composable
fun MainScreen(
    onJobClick: (String) -> Unit,
    onFilterClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onTabClick: (HomeTab) -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // 적용된 필터(직군·경력·규모) 구독 → 진입·변경 시 재조회 (빈 리스트면 전체).
    val cats = ActiveFilter.categories
    val exps = ActiveFilter.experiences
    val szs = ActiveFilter.sizes
    LaunchedEffect(cats, exps, szs) { viewModel.load(cats, exps, szs) }
    var section by remember { mutableStateOf(JobKind.NEW) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "채용알리미",
            action = {
                Row {
                    HiFiIconBtn(Icons.Outlined.NotificationsNone, "알림", onClick = onNotificationClick)
                    Spacer(Modifier.width(6.dp))
                    HiFiIconBtn(Icons.Outlined.Tune, "필터", onClick = onFilterClick)
                }
            }
        )

        when (val s = state) {
            is MainUiState.Loading -> CenterBox { CircularProgressIndicator(color = HiFiColors.Brand) }

            is MainUiState.Error -> CenterBox {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("공고를 불러오지 못했어요", style = HiFiType.body, color = HiFiColors.Text2)
                    Spacer(Modifier.height(4.dp))
                    Text(s.message, style = HiFiType.caption, color = HiFiColors.Text3)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "다시 시도",
                        style = HiFiType.body2,
                        color = HiFiColors.Brand,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { viewModel.load() }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                }
            }

            is MainUiState.Success -> SuccessContent(
                feed = s.feed,
                section = section,
                onSectionChange = { section = it },
                onJobClick = onJobClick,
            )
        }

        HiFiTabBar(active = HomeTab.Home, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

/** 성공 상태: 헤더 + 섹션 토글 + 공고 리스트. */
@Composable
private fun ColumnScope.SuccessContent(
    feed: JobRepository.TodayFeed,
    section: JobKind,
    onSectionChange: (JobKind) -> Unit,
    onJobClick: (String) -> Unit,
) {
    val counts = feed.counts
    val filtered = feed.jobs.filter { it.kind == section }

    // 헤더 (날짜 + 큰 카운트 + 마스코트)
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(Modifier.weight(1f)) {
            Text("오늘", style = HiFiType.body2, color = HiFiColors.Text2)
            Spacer(Modifier.height(2.dp))
            Row {
                Text("오늘 새 공고 ", style = HiFiType.title, color = HiFiColors.Text)
                Text("${counts[JobKind.NEW] ?: 0}건", style = HiFiType.title, color = HiFiColors.Brand)
            }
        }
        Box(contentAlignment = Alignment.TopEnd) {
            Mascot(size = 60.dp, expression = MascotExpression.Happy)
            val n = counts[JobKind.NEW] ?: 0
            if (n > 0) {
                Box(
                    Modifier
                        .offset(x = 4.dp, y = (-2).dp)
                        .height(24.dp)
                        .widthIn(min = 24.dp)
                        .clip(CircleShape)
                        .background(HiFiColors.Brand)
                        .border(2.dp, Color.White, CircleShape)
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "$n",
                        style = HiFiType.caption.copy(fontSize = 12.sp, letterSpacing = 0.sp),
                        color = Color.White,
                    )
                }
            }
        }
    }

    // NEW / UPDATE / CLOSING 토글
    Row(
        Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        JobKind.values().forEach { kind ->
            SectionChip(
                kind = kind,
                count = counts[kind] ?: 0,
                selected = section == kind,
                onClick = { onSectionChange(kind) },
                modifier = Modifier.weight(1f),
            )
        }
    }

    // 공고 리스트
    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        items(filtered, key = { it.id }) { job ->
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
        if (filtered.isEmpty()) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(top = 40.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("이 카테고리에 새 공고가 없어요", style = HiFiType.body2, color = HiFiColors.Text3)
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.CenterBox(content: @Composable () -> Unit) {
    Box(
        Modifier.weight(1f).fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun SectionChip(
    kind: JobKind,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bg = if (selected) kind.color() else HiFiColors.Bg
    val fg = if (selected) Color.White else HiFiColors.Text2
    val border = if (selected) kind.color() else HiFiColors.Border
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(bg)
            .border(2.dp, border, CircleShape)
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("${kind.label()} $count", style = HiFiType.body2.copy(fontSize = 14.sp), color = fg)
    }
}
