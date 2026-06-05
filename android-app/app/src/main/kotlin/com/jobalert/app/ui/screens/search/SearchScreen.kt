package com.jobalert.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 검색 진입 화면.
 * HiFi_Search 대응. 검색 입력박스 + 최근/인기/직군별/추천 키워드.
 * 검색박스에 직접 입력 → 결과 화면(백엔드 /jobs/search 연결됨).
 */
@Composable
fun SearchScreen(
    onSearch: (String) -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    var queryText by remember { mutableStateOf("") }
    var recent by remember { mutableStateOf(listOf("간호", "행정", "연구", "쿠팡", "사무")) }

    // 인기 검색어 — 현재 수집 데이터(공공기관·쿠팡 등)에서 실제로 결과가 나오는 키워드
    val popular = listOf(
        Triple("1", "간호", PopularTrend.Up),
        Triple("2", "연구", PopularTrend.Up),
        Triple("3", "행정", PopularTrend.New),
        Triple("4", "쿠팡", PopularTrend.Flat),
        Triple("5", "운영", PopularTrend.Down),
    )
    val byCategory = listOf(
        "IT개발·데이터", "디자인", "마케팅·홍보·조사",
        "기획·전략", "회계·세무·재무", "인사·노무·HRD",
        "영업·판매·무역", "연구·R&D",
    )
    val hashtags = listOf("#신입공채", "#수시채용", "#재택가능", "#복지좋은", "#성과급", "#스타트업")

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(title = "검색")

        // 검색 입력박스 (실제 입력 가능). 키보드 검색(↵) 누르면 결과로 이동.
        val submit = { val q = queryText.trim(); if (q.isNotEmpty()) onSearch(q) }
        Box(Modifier.padding(horizontal = 20.dp, vertical = 4.dp).fillMaxWidth()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HiFiColors.Bg2)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = HiFiColors.Text, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (queryText.isEmpty()) {
                        Text(
                            "기업명·직무·키워드",
                            style = HiFiType.body.copy(fontWeight = FontWeight.SemiBold),
                            color = HiFiColors.Text2,
                        )
                    }
                    BasicTextField(
                        value = queryText,
                        onValueChange = { queryText = it },
                        singleLine = true,
                        textStyle = HiFiType.body.copy(fontWeight = FontWeight.SemiBold, color = HiFiColors.Text),
                        cursorBrush = SolidColor(HiFiColors.Brand),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { submit() }),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (queryText.isNotEmpty()) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "지우기",
                        tint = HiFiColors.Text2,
                        modifier = Modifier.size(18.dp).clickable { queryText = "" },
                    )
                } else {
                    Icon(Icons.Outlined.Mic, contentDescription = null, tint = HiFiColors.Text2, modifier = Modifier.size(18.dp))
                }
            }
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 16.dp),
        ) {
            // 최근 검색
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("최근 검색", style = HiFiType.h2, color = HiFiColors.Text, modifier = Modifier.weight(1f))
                HiFiButton(
                    text = "전체삭제",
                    onClick = { recent = emptyList() },
                    variant = HiFiButtonVariant.Ghost,
                    size = HiFiButtonSize.Sm,
                )
            }
            Spacer(Modifier.height(8.dp))
            if (recent.isEmpty()) {
                Text("최근 검색어가 없어요", style = HiFiType.body2, color = HiFiColors.Text3)
            } else {
                RecentChipsFlow(
                    items = recent,
                    onRemove = { v -> recent = recent.filterNot { it == v } },
                    onClick = { v -> onSearch(v) },
                )
            }

            Spacer(Modifier.height(22.dp))
            Text("🔥 지금 인기 검색어", style = HiFiType.h2, color = HiFiColors.Text)
            Spacer(Modifier.height(8.dp))
            Column {
                popular.forEach { (rank, keyword, trend) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSearch(keyword) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            rank,
                            style = HiFiType.monoNum.copy(fontSize = 15.sp),
                            color = HiFiColors.Brand,
                            modifier = Modifier.width(24.dp),
                        )
                        Text(
                            keyword,
                            style = HiFiType.body.copy(fontWeight = FontWeight.SemiBold),
                            color = HiFiColors.Text,
                            modifier = Modifier.weight(1f),
                        )
                        TrendBadge(trend)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            Text("직군별 둘러보기", style = HiFiType.h2, color = HiFiColors.Text)
            Spacer(Modifier.height(10.dp))
            CategoryGrid(items = byCategory, onClick = { onSearch(it) })

            Spacer(Modifier.height(22.dp))
            Text("추천 키워드", style = HiFiType.h2, color = HiFiColors.Text)
            Spacer(Modifier.height(10.dp))
            HashtagFlow(items = hashtags, onClick = { onSearch(it) })
        }

        HiFiTabBar(active = HomeTab.Search, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

private enum class PopularTrend { Up, Down, Flat, New }

@Composable
private fun TrendBadge(t: PopularTrend) {
    val (text, color) = when (t) {
        PopularTrend.Up -> "▲" to HiFiColors.NewShadow
        PopularTrend.Down -> "▼" to HiFiColors.Closing
        PopularTrend.New -> "NEW" to HiFiColors.Brand
        PopularTrend.Flat -> "-" to HiFiColors.Text3
    }
    Text(text, style = HiFiType.caption.copy(fontSize = 11.sp, letterSpacing = 0.sp), color = color)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentChipsFlow(
    items: List<String>,
    onRemove: (String) -> Unit,
    onClick: (String) -> Unit,
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items.forEach { v ->
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(HiFiColors.Bg2)
                    .clickable { onClick(v) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(v, style = HiFiType.body2, color = HiFiColors.Text)
                Spacer(Modifier.width(6.dp))
                Box(
                    Modifier
                        .size(14.dp)
                        .clickable { onRemove(v) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "$v 삭제",
                        tint = HiFiColors.Text3,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryGrid(items: List<String>, onClick: (String) -> Unit) {
    val rows = items.chunked(2)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                row.forEach { label ->
                    HiFiButton(
                        text = label,
                        onClick = { onClick(label) },
                        variant = HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                        fullWidth = true,
                        maxLines = 1,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HashtagFlow(items: List<String>, onClick: (String) -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items.forEach { v ->
            HiFiChip(text = v, variant = HiFiChipVariant.Outline, small = true, onClick = { onClick(v) })
        }
    }
}
