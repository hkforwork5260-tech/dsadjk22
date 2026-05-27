package com.jobalert.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.api.CompanyDto
import com.jobalert.app.data.api.MockApi
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 온보딩 ③ 회사 스와이프 (Reels 스타일).
 * HiFi_OnbSwipe 대응. VerticalPager로 1페이지 = 1회사.
 * 마지막 페이지는 "다음" CTA.
 */
@Composable
fun OnboardingCompanySwipeScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit,
) {
    val companies = remember { MockApi.popularCompanies().companies }
    var favSet by remember { mutableStateOf(setOf<Int>()) }
    var savedJobs by remember { mutableStateOf(setOf<String>()) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "관심 회사 고르기",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
            action = {
                HiFiButton(
                    text = "건너뛰기",
                    onClick = onSkip,
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            },
        )

        Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
            OnboardingDots(total = 4, activeIndex = 2)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "스크롤하면서 ❤️ 누르면 관심기업으로!",
                    style = HiFiType.body2,
                    color = HiFiColors.Text2,
                    modifier = Modifier.weight(1f),
                )
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(HiFiColors.BrandSoft)
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                ) {
                    Text(
                        "${favSet.size}개 추가됨",
                        style = HiFiType.caption,
                        color = HiFiColors.BrandDark,
                    )
                }
            }
        }

        // LazyColumn + SnapFlingBehavior → 50% 넘기면 다음 페이지로 자동 snap (릴스 느낌).
        // VerticalPager 대신 LazyColumn 쓰는 이유: 에뮬레이터 마우스 휠/드래그 호환성.
        val listState = rememberLazyListState()
        val snapFling = rememberSnapFlingBehavior(listState)
        LazyColumn(
            state = listState,
            flingBehavior = snapFling,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) {
            items(companies, key = { it.id }) { c ->
                SwipeCardSlot {
                    SwipeCompanyCard(
                        company = c,
                        isFav = c.id in favSet,
                        isSaved = "job-of-${c.id}" in savedJobs,
                        onToggleFav = {
                            favSet = if (c.id in favSet) favSet - c.id else favSet + c.id
                        },
                        onToggleSave = {
                            val k = "job-of-${c.id}"
                            savedJobs = if (k in savedJobs) savedJobs - k else savedJobs + k
                        },
                        pageIndex = companies.indexOf(c),
                        pageTotal = companies.size,
                    )
                }
            }
            item(key = "finish") {
                SwipeCardSlot {
                    FinishCard(favCount = favSet.size, onNext = onNext)
                }
            }
        }
        HiFiGestureNav()
    }
}

/** 각 카드를 한 화면 풀로 채우는 LazyColumn item slot. */
@Composable
private fun LazyItemScope.SwipeCardSlot(content: @Composable () -> Unit) {
    Box(Modifier.fillParentMaxSize()) {
        content()
    }
}

@Composable
private fun SwipeCompanyCard(
    company: CompanyDto,
    isFav: Boolean,
    isSaved: Boolean,
    onToggleFav: () -> Unit,
    onToggleSave: () -> Unit,
    pageIndex: Int,
    pageTotal: Int,
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(HiFiColors.Bg)
            .border(2.dp, HiFiColors.Border, RoundedCornerShape(24.dp)),
    ) {
        Column(Modifier.fillMaxSize()) {
            // 상단 헤더
            Row(
                Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(HiFiColors.BrandSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(company.logo, style = HiFiType.h2, color = HiFiColors.BrandDark)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "${sizeLabel(company.size)} · ${company.industry}",
                        style = HiFiType.body2.copy(fontSize = 12.sp),
                        color = HiFiColors.Text2,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(company.name, style = HiFiType.title, color = HiFiColors.Text)
                }
            }

            // 태그 행 (가로 스크롤)
            val hScroll = rememberScrollState()
            Row(
                Modifier
                    .padding(horizontal = 20.dp)
                    .horizontalScroll(hScroll),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                HiFiChip("#${company.industry}", small = true)
                HiFiChip("#${sizeLabel(company.size)}", small = true)
                if (company.group.isNotBlank()) HiFiChip("#${company.group}", small = true)
                Box(
                    Modifier
                        .clip(CircleShape)
                        .background(HiFiColors.BrandSoft)
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                ) {
                    Text(
                        "최근 공고 ${company.activeJobCount}건",
                        style = HiFiType.caption,
                        color = HiFiColors.BrandDark,
                    )
                }
            }

            Box(
                Modifier
                    .padding(horizontal = 20.dp, vertical = 18.dp)
                    .height(1.dp)
                    .fillMaxWidth()
                    .background(HiFiColors.Border),
            )

            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HiFiLabel(text = "NEW", bg = HiFiColors.New)
                    Spacer(Modifier.width(8.dp))
                    Text("D-22", style = HiFiType.monoNum.copy(fontSize = 13.sp), color = HiFiColors.New)
                    Spacer(Modifier.weight(1f))
                    Text("~6/13 18:00", style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    sampleRole(company),
                    style = HiFiType.display.copy(fontSize = 22.sp, lineHeight = 26.sp),
                    color = HiFiColors.Text,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HiFiChip("📍 ${cityFor(company)}", small = true, variant = HiFiChipVariant.Outline)
                    HiFiChip("🎓 학사+", small = true, variant = HiFiChipVariant.Outline)
                    HiFiChip("💼 신입", small = true, variant = HiFiChipVariant.Outline)
                }
                Spacer(Modifier.height(14.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(HiFiColors.BrandSoft)
                        .border(2.dp, HiFiColors.Brand, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text("✨", style = HiFiType.h2.copy(fontSize = 18.sp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            sampleSummary(company),
                            style = HiFiType.body.copy(fontWeight = FontWeight.SemiBold),
                            color = HiFiColors.Text,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (pageIndex < pageTotal - 1) {
                    Text(
                        "↓ 다음 회사로 스크롤",
                        style = HiFiType.caption,
                        color = HiFiColors.Text3,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 18.dp),
                    )
                } else {
                    Spacer(Modifier.height(18.dp))
                }
            }
        }

        // 우측 액션 (관심/저장)
        Column(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircleActionButton(
                icon = if (isFav) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                contentDesc = "관심기업",
                activeColor = HiFiColors.Brand,
                active = isFav,
                onClick = onToggleFav,
                label = "관심기업",
            )
            CircleActionButton(
                icon = Icons.Outlined.BookmarkBorder,
                contentDesc = "공고 저장",
                activeColor = HiFiColors.Update,
                active = isSaved,
                onClick = onToggleSave,
                label = "공고 저장",
            )
        }

        // 좌상단 progress bar
        Row(
            Modifier
                .align(Alignment.TopStart)
                .padding(start = 18.dp, top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            repeat(pageTotal) { i ->
                val active = i <= pageIndex
                Box(
                    Modifier
                        .width(if (i == pageIndex) 16.dp else 6.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(if (active) HiFiColors.Brand else HiFiColors.Border),
                )
            }
        }
    }
}

@Composable
private fun CircleActionButton(
    icon: ImageVector,
    contentDesc: String,
    activeColor: Color,
    active: Boolean,
    onClick: () -> Unit,
    label: String,
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
                contentDescription = contentDesc,
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
private fun FinishCard(favCount: Int, onNext: () -> Unit) {
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
                if (favCount > 0) "${favCount}개 관심기업 추가!" else "관심 회사를 골라봐!",
                style = HiFiType.title,
                color = HiFiColors.BrandDark,
            )
            Spacer(Modifier.height(6.dp))
            Text("매일 새 공고를 알려드릴게요", style = HiFiType.body2, color = HiFiColors.Text2)
            Spacer(Modifier.height(22.dp))
            HiFiButton(text = "다음", onClick = onNext, variant = HiFiButtonVariant.Primary, fullWidth = true)
        }
    }
}

private fun sizeLabel(code: String): String = when (code) {
    "large_corp" -> "대기업"
    "mid_corp" -> "중견기업"
    "small" -> "중소기업"
    "public" -> "공기업"
    "startup" -> "스타트업"
    "foreign" -> "외국계"
    else -> "기업"
}

private fun cityFor(c: CompanyDto): String = when (c.name) {
    "두산에너빌리티" -> "창원"
    "KT" -> "판교"
    "아모레퍼시픽" -> "서울 용산"
    "한화시스템" -> "성남"
    "쿠팡" -> "서울 송파"
    else -> "서울"
}

private fun sampleRole(c: CompanyDto): String = when (c.name) {
    "두산에너빌리티" -> "2026 신입공채 (기계/전기/화학)"
    "KT" -> "AI Lab 연구원 (신입)"
    "아모레퍼시픽" -> "마케팅·브랜드 매니저"
    "한화시스템" -> "SW 엔지니어 (신입)"
    "쿠팡" -> "백엔드 엔지니어 (신입)"
    else -> "신입 채용"
}

private fun sampleSummary(c: CompanyDto): String = when (c.name) {
    "두산에너빌리티" -> "배터리·원전·풍력 등 다양한 분야에서 신입 모집. 학사 이상."
    "KT" -> "GPT 기반 한국어 LLM 연구·개발. 석사+ 우대."
    "아모레퍼시픽" -> "설화수·라네즈 등 메인 브랜드 마케팅 담당."
    "한화시스템" -> "위성·방산 SW 개발. C++/Python 기본."
    "쿠팡" -> "대규모 트래픽 처리. Java/Kotlin · MSA."
    else -> "신입 공채. 학사 이상."
}
