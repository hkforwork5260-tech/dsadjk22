package com.jobalert.app.ui.screens.discover

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.SeenJobs
import com.jobalert.app.data.model.Job
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType
import com.jobalert.app.ui.theme.color
import com.jobalert.app.ui.theme.label
import com.jobalert.app.ui.theme.softColor

/**
 * 찾아보기 (Reels 스타일 디스커버리).
 * VerticalPager로 1페이지 = 1공고. 온보딩 ③ 스와이프 패턴 재사용.
 * 마지막 페이지는 "다 봤어!" finish 카드.
 */
@Composable
fun DiscoverScreen(
    onJobClick: (String) -> Unit,
    onShare: () -> Unit,
    onFilter: () -> Unit,
    onGoMain: () -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    val viewModel: DiscoverViewModel = viewModel()
    // 메인과 동일하게 ActiveFilter(직군·경력·규모)를 구독해 변경 시 재조회. (이전엔 필터 무시)
    val cats = ActiveFilter.categories
    val exps = ActiveFilter.experiences
    val szs = ActiveFilter.sizes
    LaunchedEffect(cats, exps, szs) { viewModel.load(cats, exps, szs) }
    val jobs by viewModel.jobs.collectAsStateWithLifecycle()
    val favoriteCompanyIds by viewModel.favoriteCompanyIds.collectAsStateWithLifecycle()
    val savedJobIds by viewModel.savedJobIds.collectAsStateWithLifecycle()
    // 이미 본 공고는 뒤로(안 본 것 먼저). 로드 시점 스냅샷이라 보는 도중엔 순서가 안 바뀐다.
    val orderedJobs = remember(jobs) { jobs.sortedBy { it.id in SeenJobs.seenIds } }
    val pageCount = orderedJobs.size + 1
    val pagerState = rememberPagerState(pageCount = { pageCount })
    // '본 것' 기록은 스크롤이 아니라 '자세히 보기'로 실제 진입했을 때만(onOpenDetail). → 직접 본 공고만 후순위.

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "찾아보기",
            action = { HiFiIconBtn(Icons.Outlined.Tune, "필터", onClick = onFilter) },
        )

        val flingBehavior = PagerDefaults.flingBehavior(
            state = pagerState,
            snapPositionalThreshold = 0.15f,
            snapAnimationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioNoBouncy),
        )
        VerticalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            pageSize = PageSize.Fill,
            flingBehavior = flingBehavior,
            beyondViewportPageCount = 2,
        ) { page ->
            if (page < orderedJobs.size) {
                val j = orderedJobs[page]
                ReelsJobCard(
                    job = j,
                    isFav = j.companyId != null && j.companyId in favoriteCompanyIds,
                    isSaved = j.id in savedJobIds,
                    onToggleFav = { j.companyId?.let { viewModel.toggleFavorite(it) } },
                    onToggleSave = { viewModel.toggleSave(j.id) },
                    onShare = onShare,
                    onOpenDetail = { SeenJobs.markSeen(j.id); onJobClick(j.id) },
                    pageIndex = page,
                    pageTotal = orderedJobs.size,
                )
            } else {
                FinishReelsCard(favCount = favoriteCompanyIds.size, savedCount = savedJobIds.size, onGoMain = onGoMain)
            }
        }

        HiFiTabBar(active = HomeTab.Discover, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

@Composable
private fun ReelsJobCard(
    job: Job,
    isFav: Boolean,
    isSaved: Boolean,
    onToggleFav: () -> Unit,
    onToggleSave: () -> Unit,
    onShare: () -> Unit,
    onOpenDetail: () -> Unit,
    pageIndex: Int,
    pageTotal: Int,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HiFiColors.Bg)
            .border(2.dp, HiFiColors.Border, RoundedCornerShape(24.dp))
            .clickable(onClick = onOpenDetail),
    ) {
        Column(Modifier.fillMaxSize()) {
            // 상단 헤더 (로고 + 회사 + 종류 라벨)
            Row(
                Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(job.kind.softColor()),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(job.logo, style = HiFiType.h2.copy(fontSize = 22.sp), color = job.kind.color())
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    HiFiLabel(text = job.kind.label(), bg = job.kind.color())
                    Spacer(Modifier.height(6.dp))
                    Text(job.company, style = HiFiType.title, color = HiFiColors.Text)
                    if (job.location.isNotBlank()) {
                        Text(
                            "📍 ${job.location}",
                            style = HiFiType.body2.copy(fontSize = 12.sp),
                            color = HiFiColors.Text2,
                        )
                    }
                }
            }

            // 직무 + D-day. 우측 액션버튼 영역(약 72dp)을 비워 글자가 버튼 밑으로 안 파고들게.
            Column(Modifier.padding(start = 20.dp, end = 72.dp)) {
                Text(
                    job.role,
                    style = HiFiType.display.copy(fontSize = 20.sp, lineHeight = 25.sp),
                    color = HiFiColors.Text,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(job.kind.softColor())
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Text(
                            job.dday,
                            style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                            color = job.kind.color(),
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        job.dateText,
                        style = HiFiType.body2.copy(fontSize = 13.sp),
                        color = HiFiColors.Text2,
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // 칩 (가로 스크롤) — 채워진 정보만 노출(경력·회사규모·직군·학력·태그 순)
            val hScroll = rememberScrollState()
            Row(
                Modifier
                    .padding(start = 20.dp, end = 72.dp)
                    .horizontalScroll(hScroll),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                if (job.salary.isNotBlank()) HiFiChip("💰 ${job.salary}", small = true, variant = HiFiChipVariant.Outline)
                if (job.experience.isNotBlank()) HiFiChip("💼 ${job.experience}", small = true, variant = HiFiChipVariant.Outline)
                companySizeLabel(job.companySize)?.let { HiFiChip("🏢 $it", small = true, variant = HiFiChipVariant.Outline) }
                job.categories.take(2).forEach { HiFiChip(it, small = true) }
                if (job.education.isNotBlank()) HiFiChip("🎓 ${job.education}", small = true, variant = HiFiChipVariant.Outline)
                job.tags.forEach { HiFiChip("#$it", small = true) }
            }

            // 본문 미리보기 — 카드 여백을 채우고 공고 감을 준다(수집된 경우만).
            if (job.description.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    job.description,
                    modifier = Modifier.padding(start = 20.dp, end = 72.dp),
                    style = HiFiType.body2.copy(fontSize = 14.sp, lineHeight = 21.sp),
                    color = HiFiColors.Text2,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Spacer(Modifier.weight(1f))

            // 안내 + 자세히 보기 CTA
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                HiFiButton(
                    text = "자세히 보기",
                    onClick = onOpenDetail,
                    variant = HiFiButtonVariant.Primary,
                    size = HiFiButtonSize.Md,
                    fullWidth = true,
                )
                Spacer(Modifier.height(8.dp))
                if (pageIndex < pageTotal - 1) {
                    Text(
                        "↓ 다음 공고로 스크롤",
                        style = HiFiType.caption,
                        color = HiFiColors.Text3,
                    )
                } else {
                    Text(
                        "마지막 페이지에요",
                        style = HiFiType.caption,
                        color = HiFiColors.Text3,
                    )
                }
            }
        }

        // 우측 액션
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircleAction(
                icon = if (isFav) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                desc = "관심기업",
                activeColor = HiFiColors.Closing,
                active = isFav,
                label = "관심기업",
                onClick = onToggleFav,
            )
            CircleAction(
                icon = Icons.Outlined.BookmarkBorder,
                desc = "저장",
                activeColor = HiFiColors.Update,
                active = isSaved,
                label = "저장",
                onClick = onToggleSave,
            )
            CircleAction(
                icon = Icons.Outlined.Share,
                desc = "공유",
                activeColor = HiFiColors.Info,
                active = false,
                label = "공유",
                onClick = onShare,
            )
        }
    }
}

@Composable
private fun CircleAction(
    icon: ImageVector,
    desc: String,
    activeColor: Color,
    active: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (active) activeColor else Color.White)
                .border(2.dp, if (active) activeColor else HiFiColors.Border, CircleShape)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = desc,
                tint = if (active) Color.White else HiFiColors.Text2,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = HiFiType.caption.copy(fontSize = 10.sp, letterSpacing = 0.sp),
            color = if (active) activeColor else HiFiColors.Text3,
        )
    }
}

@Composable
private fun FinishReelsCard(favCount: Int, savedCount: Int, onGoMain: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HiFiColors.BrandSoft),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(28.dp),
        ) {
            Mascot(size = 120.dp, expression = MascotExpression.Happy)
            Spacer(Modifier.height(14.dp))
            Text(
                "오늘은 여기까지!",
                style = HiFiType.title,
                color = HiFiColors.BrandDark,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                if (favCount + savedCount > 0)
                    "좋아요 ${favCount}개 · 저장 ${savedCount}개"
                else
                    "내일 또 새로운 공고로 찾아올게요",
                style = HiFiType.body2,
                color = HiFiColors.Text2,
            )
            Spacer(Modifier.height(22.dp))
            HiFiButton(
                text = "홈으로 돌아가기",
                onClick = onGoMain,
                variant = HiFiButtonVariant.Primary,
                fullWidth = true,
            )
        }
    }
}

/** 회사규모 코드 → 한글 라벨. 빈/모르는 값은 null(배지 미노출). DB 실제값: large_corp·public·startup_unicorn. */
private fun companySizeLabel(code: String): String? = when (code) {
    "large_corp" -> "대기업"
    "mid_corp" -> "중견기업"
    "small" -> "중소기업"
    "public" -> "공기업"
    "startup", "startup_unicorn" -> "스타트업"
    "foreign" -> "외국계"
    else -> null
}
