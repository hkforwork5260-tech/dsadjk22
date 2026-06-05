package com.jobalert.app.ui.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.FavoriteCompanyDto
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 관심기업 그리드.
 * HiFi_Favorites 대응. 3열 그리드 + "기업 추가" 점선 카드 + 안내 카드.
 * 로고 우상단 빨간 뱃지 = 오늘 새 공고 N건 / 좌하단 🔔 = 알림 켜짐.
 */
@Composable
fun FavoritesScreen(
    onCompanyClick: (Int) -> Unit,
    onAddCompany: () -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    // 백엔드 관심기업(기기 기준). 진입할 때마다 새로고침(추가/삭제 반영).
    val viewModel: FavoritesViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.load() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favs = (state as? FavoritesUiState.Success)?.companies ?: emptyList()
    val totalNew = favs.sumOf { it.newCount }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "관심 기업",
            action = {
                HiFiIconBtn(Icons.Outlined.Add, "기업 추가", onClick = onAddCompany)
            },
        )

        // 상단 요약
        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text("관심 기업 ${favs.size}", style = HiFiType.h2, color = HiFiColors.Text)
                if (totalNew > 0) {
                    Text(" · ", style = HiFiType.h2, color = HiFiColors.Text2)
                    Text("오늘 새공고 ${totalNew}건", style = HiFiType.h2, color = HiFiColors.Brand)
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                "로고 우상단 빨간 뱃지 = 오늘 새 공고",
                style = HiFiType.body2,
                color = HiFiColors.Text2,
            )
        }

        // 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(favs, key = { it.company.id }) { fav ->
                FavoriteCard(fav = fav, onClick = { onCompanyClick(fav.company.id) })
            }
            item {
                AddCompanyCard(onClick = onAddCompany)
            }

            // 안내 카드 (그리드 전체 폭)
            item(span = { GridItemSpan(maxLineSpan) }) {
                MascotHint()
            }
        }

        HiFiTabBar(active = HomeTab.Favorites, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

@Composable
private fun FavoriteCard(fav: FavoriteCompanyDto, onClick: () -> Unit) {
    val hasNew = fav.newCount > 0
    val borderColor = if (hasNew) HiFiColors.Brand else HiFiColors.Border
    val logoBg = if (hasNew) HiFiColors.BrandSoft else HiFiColors.Bg2

    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, borderColor, RoundedCornerShape(14.dp))
            .background(HiFiColors.Bg)
            .clickable(onClick = onClick),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(logoBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    fav.company.logo,
                    style = HiFiType.body2.copy(fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
                    color = if (hasNew) HiFiColors.BrandDark else HiFiColors.Text,
                )
            }
            Text(
                fav.company.name,
                style = HiFiType.body.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                color = HiFiColors.Text,
                textAlign = TextAlign.Center,
                maxLines = 2,
            )
            Text(
                "공고 ${fav.company.activeJobCount}",
                style = HiFiType.body2.copy(fontSize = 11.sp),
                color = HiFiColors.Text2,
            )
        }

        // 우상단 NEW 뱃지
        if (hasNew) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .heightIn(min = 20.dp)
                    .widthIn(min = 20.dp)
                    .clip(CircleShape)
                    .background(HiFiColors.Brand)
                    .border(2.dp, Color.White, CircleShape)
                    .padding(horizontal = 5.dp, vertical = 1.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${fav.newCount}",
                    style = HiFiType.caption.copy(fontSize = 11.sp, letterSpacing = 0.sp),
                    color = Color.White,
                )
            }
        }

        // 좌하단 🔔 알림 표식
        if (fav.hasAlarm) {
            Text(
                "🔔",
                style = HiFiType.body2.copy(fontSize = 11.sp),
                modifier = Modifier.align(Alignment.BottomStart).padding(4.dp),
            )
        }
    }
}

@Composable
private fun AddCompanyCard(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp)
            .clip(RoundedCornerShape(14.dp))
            .dashedBorder(HiFiColors.BorderDark)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = "기업 추가",
                tint = HiFiColors.Text3,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.height(4.dp))
            Text("기업 추가", style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
        }
    }
}

@Composable
private fun MascotHint() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(HiFiColors.BrandSoft)
            .border(2.dp, HiFiColors.Brand, RoundedCornerShape(16.dp))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Mascot(size = 40.dp, expression = MascotExpression.Default)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    "로고를 누르면 그 회사 공고만 모아볼 수 있어요",
                    style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                    color = HiFiColors.Text,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "꾹 누르면 알림 끄기 / 삭제",
                    style = HiFiType.body2.copy(fontSize = 12.sp),
                    color = HiFiColors.Text2,
                )
            }
        }
    }
}

/**
 * 점선 테두리 Modifier. Compose 기본 border는 점선을 지원하지 않으므로 dashEffect를 직접 적용.
 */
private fun Modifier.dashedBorder(color: Color): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f),
    )
    drawRoundRect(
        color = color,
        style = stroke,
        cornerRadius = CornerRadius(14.dp.toPx()),
    )
}

