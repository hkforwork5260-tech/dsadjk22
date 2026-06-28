package com.jobalert.app.ui.screens.companysearch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.CompanyDto
import com.jobalert.app.ui.components.HiFiAppBar
import com.jobalert.app.ui.components.HiFiIconBtn
import com.jobalert.app.ui.components.HiFiStatusBar
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 관심기업 추가 — 회사명으로 검색해 ★로 관심기업에 담는다.
 * 마이페이지 관심기업 화면의 "기업 추가"에서 진입(기존엔 공고 검색으로 빠져 회사를 못 골랐음).
 */
@Composable
fun CompanySearchScreen(onBack: () -> Unit) {
    val vm: CompanySearchViewModel = viewModel()
    val state by vm.state.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "관심기업 추가",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
        )

        // 검색 입력
        Row(
            Modifier
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(HiFiColors.Bg2)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = HiFiColors.Text2)
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                if (state.query.isEmpty()) {
                    Text("회사명을 검색하세요 (예: 삼성, 토스)", style = HiFiType.body, color = HiFiColors.Text2)
                }
                BasicTextField(
                    value = state.query,
                    onValueChange = vm::onQueryChange,
                    singleLine = true,
                    textStyle = HiFiType.body.copy(color = HiFiColors.Text),
                    cursorBrush = SolidColor(HiFiColors.Brand),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        when {
            state.query.isBlank() -> EmptyHint("관심 있는 회사를 검색해\n★로 담아보세요.")
            state.results.isEmpty() && !state.loading ->
                EmptyHint("'${state.query}' 검색 결과가 없어요.\n진행 중인 공고가 있는 회사만 나와요.")
            else -> LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.results, key = { it.id }) { company ->
                    CompanyRow(company, onToggle = { vm.toggleFavorite(company) })
                }
            }
        }
    }
}

@Composable
private fun CompanyRow(company: CompanyDto, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(HiFiColors.Bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(HiFiColors.Bg2),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                company.logo.ifBlank { company.name.take(2) },
                style = HiFiType.body2.copy(fontWeight = FontWeight.ExtraBold, fontSize = 13.sp),
                color = HiFiColors.Text,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(company.name, style = HiFiType.title, color = HiFiColors.Text)
            Text("진행 중 ${company.activeJobCount}개 공고", style = HiFiType.caption, color = HiFiColors.Text2)
        }
        Box(
            Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                if (company.isFavorited) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                contentDescription = if (company.isFavorited) "관심기업 해제" else "관심기업 추가",
                tint = if (company.isFavorited) HiFiColors.Brand else HiFiColors.Text2,
            )
        }
    }
}

@Composable
private fun ColumnScope.EmptyHint(text: String) {
    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
        Text(
            text,
            style = HiFiType.body.copy(fontSize = 14.sp),
            color = HiFiColors.Text2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}
