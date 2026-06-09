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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.jobalert.app.data.HelpState
import com.jobalert.app.data.JobRepository
import com.jobalert.app.data.model.regionShort
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.theme.*
import com.jobalert.app.widget.JobAlertWidgetProvider
import com.jobalert.app.widget.WidgetState

/**
 * 메인 피드. 상단 헤더(오늘 새 공고 N건 + 단이) + NEW/UPDATE/CLOSING 토글 + 공고 리스트.
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
    val dday = ActiveFilter.deadlineDays
    LaunchedEffect(cats, exps, szs, dday) { viewModel.load(cats, exps, szs, dday) }
    // 앱에 다시 들어올 때마다 현재 시각 기준으로 재조회 — 오전엔 열렸던 게 오후엔 마감됐을 수 있으니.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.load(cats, exps, szs, dday)
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }
    var section by remember { mutableStateOf(JobKind.NEW) }

    // 오늘 새 공고(NEW) 수를 위젯에 반영(단이 표정·카운트).
    val context = LocalContext.current
    LaunchedEffect(state) {
        (state as? MainUiState.Success)?.feed?.let { f ->
            // 위젯·오늘·알림 통일 기준: '오늘(KST) 처음 올라온 공고'(서버 kind=NEW)만 센다.
            // '안 본 공고'는 서버(알림)가 알 수 없어 셋을 못 맞추므로 새 공고 정의에서 제외.
            val newCount = f.jobs.count { it.kind == JobKind.NEW }
            val closingCount = f.jobs.count { it.kind == JobKind.CLOSING }
            val topJob = f.jobs.firstOrNull()?.let { "${it.company} · ${it.role}" } ?: ""
            WidgetState.setSummary(context, newCount, closingCount, topJob)
            JobAlertWidgetProvider.updateAll(context)
        }
    }

    // 처음 진입 시 도움말 1회 자동 노출. 앱바 '?'로 다시 보기.
    var showHelp by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!HelpState.shown(context, "today")) { showHelp = true; HelpState.markShown(context, "today") }
    }
    if (showHelp) {
        HelpDialog(
            title = "오늘 탭이란?",
            lines = listOf(
                "'관심'(직군·회사 규모)에 맞는 새 공고를 매일 모아줘요.",
                "필터는 1회성이에요 — 앱을 닫으면 풀려요. 매일 받아보려면 마이페이지 '관심'에서 바꿔주세요.",
                "NEW = 오늘 새로 올라온 공고 · 마감임박 = 마감 3일 이내.",
            ),
            onDismiss = { showHelp = false },
        )
    }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "채용알리미",
            action = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HelpIconButton(onClick = { showHelp = true })
                    Spacer(Modifier.width(6.dp))
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
    // NEW = 오늘(KST) 처음 올라온 공고(서버 kind=NEW)만. 위젯·알림과 동일 기준(셋이 같은 숫자).
    //   오늘 올라온 건 봐도 하루 유지. 마감임박·변경은 각 칸에서 봄.
    val newList = feed.jobs.filter { it.kind == JobKind.NEW }
    val updateList = feed.jobs.filter { it.kind == JobKind.UPDATE }
    // 마감임박은 마감 이른 순 정렬.
    val closingList = feed.jobs.filter { it.kind == JobKind.CLOSING }.sortedBy { j -> ddayNum(j.dday) }
    fun countOf(k: JobKind) = when (k) {
        JobKind.NEW -> newList.size
        JobKind.UPDATE -> updateList.size
        JobKind.CLOSING -> closingList.size
        else -> 0
    }
    val filtered = when (section) {
        JobKind.NEW -> newList
        JobKind.UPDATE -> updateList
        JobKind.CLOSING -> closingList
        else -> emptyList()
    }

    // 헤더 (날짜 + 큰 카운트 + 마스코트)
    Row(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(Modifier.weight(1f)) {
            Text("오늘", style = HiFiType.body2, color = HiFiColors.Text2)
            Spacer(Modifier.height(2.dp))
            Row {
                Text("새 공고 ", style = HiFiType.title, color = HiFiColors.Text)
                Text("${newList.size}건", style = HiFiType.title, color = HiFiColors.Brand)
            }
        }
        Box(contentAlignment = Alignment.TopEnd) {
            Mascot(size = 60.dp, expression = MascotExpression.Happy)
            val n = newList.size
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
        listOf(JobKind.NEW, JobKind.UPDATE, JobKind.CLOSING).forEach { kind ->
            SectionChip(
                kind = kind,
                label = sectionLabel(kind),
                count = countOf(kind),
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
                logo = job.regionShort,   // 로고 자리에 근무지역
                dday = job.dday,
                dateText = job.dateText,
                onClick = { onJobClick(job.id) },
            )
        }
        if (filtered.isEmpty()) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(top = 48.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Mascot(size = 96.dp, expression = MascotExpression.Sleep)
                    Spacer(Modifier.height(14.dp))
                    Text("조건에 맞는 공고가 없어요", style = HiFiType.h2, color = HiFiColors.Text)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "필터를 넓히거나 '관심'을 바꿔보세요",
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                    )
                }
            }
        }
    }
}

/** dday 문자열 → 정렬용 일수. "D-Day"=0, "D-3"=3, 상시·마감 등은 뒤로(MAX). */
private fun ddayNum(d: String): Int = when {
    d == "D-Day" -> 0
    d.startsWith("D-") -> d.removePrefix("D-").toIntOrNull() ?: Int.MAX_VALUE
    else -> Int.MAX_VALUE
}

@Composable
private fun ColumnScope.CenterBox(content: @Composable () -> Unit) {
    Box(
        Modifier.weight(1f).fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) { content() }
}

/** 오늘 위 3개 토글 칩 라벨(카드의 kind 배지와 분리). 마감임박 칩은 'Hurry up!'. */
private fun sectionLabel(kind: JobKind): String = when (kind) {
    JobKind.NEW -> "NEW"
    JobKind.UPDATE -> "UPDATE"
    JobKind.CLOSING -> "Hurry up!"
    JobKind.ACTIVE -> "진행중"
}

@Composable
private fun SectionChip(
    kind: JobKind,
    label: String,
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
        Text("$label $count", style = HiFiType.body2.copy(fontSize = 14.sp), color = fg)
    }
}
