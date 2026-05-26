package com.jobalert.app.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Favorite
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.data.api.CompanyDto
import com.jobalert.app.data.api.MockApi
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
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
    onCompanyClick: (Int) -> Unit,
    onTabClick: (HomeTab) -> Unit,
) {
    val effectiveQuery = if (query.isBlank()) "삼성" else query
    val response = remember(effectiveQuery) { MockApi.jobsSearch(effectiveQuery) }
    var section by remember { mutableStateOf(ResultSection.All) }
    var favSet by remember { mutableStateOf(setOf(response.companies.firstOrNull()?.id ?: -1)) }

    val total = response.companies.size + response.jobs.size
    val showCompanies = section == ResultSection.All || section == ResultSection.Companies
    val showJobs = section == ResultSection.All || section == ResultSection.Jobs

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
                    effectiveQuery,
                    style = HiFiType.body.copy(fontWeight = FontWeight.Bold),
                    color = HiFiColors.Text,
                    modifier = Modifier.weight(1f),
                )
                Box(Modifier.clickable { onBack() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Close, contentDescription = "검색어 지우기", tint = HiFiColors.Text2, modifier = Modifier.size(14.dp))
                }
            }
        }

        // 섹션 토글
        Box(
            Modifier
                .padding(horizontal = 20.dp)
                .padding(top = 8.dp)
                .fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                HiFiChip(
                    text = "전체 $total",
                    selected = section == ResultSection.All,
                    onClick = { section = ResultSection.All },
                )
                HiFiChip(
                    text = "기업 ${response.companies.size}",
                    selected = section == ResultSection.Companies,
                    variant = HiFiChipVariant.Outline,
                    onClick = { section = ResultSection.Companies },
                )
                HiFiChip(
                    text = "공고 ${response.jobs.size}",
                    selected = section == ResultSection.Jobs,
                    variant = HiFiChipVariant.Outline,
                    onClick = { section = ResultSection.Jobs },
                )
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
            if (showCompanies && response.companies.isNotEmpty()) {
                Text(
                    "기업 (${response.companies.size})",
                    style = HiFiType.caption,
                    color = HiFiColors.Text2,
                )
                Spacer(Modifier.height(8.dp))
                Column {
                    response.companies.forEachIndexed { i, c ->
                        CompanyRow(
                            company = c,
                            query = effectiveQuery,
                            isFav = c.id in favSet,
                            onClick = { onCompanyClick(c.id) },
                            onToggleFav = {
                                favSet = if (c.id in favSet) favSet - c.id else favSet + c.id
                            },
                        )
                        if (i < response.companies.lastIndex) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(HiFiColors.Border))
                        }
                    }
                }
            }

            if (showJobs && response.jobs.isNotEmpty()) {
                Spacer(Modifier.height(22.dp))
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
            }

            if (!showCompanies && response.companies.isNotEmpty() && response.jobs.isEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("일치하는 공고가 없어요", style = HiFiType.body2, color = HiFiColors.Text3)
            }
        }

        HiFiTabBar(active = HomeTab.Search, onTabClick = onTabClick)
        HiFiGestureNav()
    }
}

private enum class ResultSection { All, Companies, Jobs }

@Composable
private fun CompanyRow(
    company: CompanyDto,
    query: String,
    isFav: Boolean,
    onClick: () -> Unit,
    onToggleFav: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(HiFiColors.Bg2),
            contentAlignment = Alignment.Center,
        ) {
            Text(company.logo, style = HiFiType.body2.copy(fontWeight = FontWeight.ExtraBold, fontSize = 12.sp), color = HiFiColors.Text)
        }
        Column(Modifier.weight(1f)) {
            // 검색어 하이라이트
            Text(highlight(company.name, query), style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text)
            if (company.activeJobCount > 0) {
                Text(
                    "오늘 공고 ${company.activeJobCount}건",
                    style = HiFiType.body2.copy(fontSize = 12.sp),
                    color = HiFiColors.Text2,
                )
            }
        }
        if (isFav) {
            Icon(
                Icons.Outlined.Favorite,
                contentDescription = "관심기업",
                tint = HiFiColors.Brand,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onToggleFav),
            )
        } else {
            HiFiButton(
                text = "관심+",
                onClick = onToggleFav,
                variant = HiFiButtonVariant.Default,
                size = HiFiButtonSize.Sm,
            )
        }
    }
}

private fun highlight(text: String, query: String) = buildAnnotatedString {
    if (query.isBlank() || !text.contains(query)) {
        append(text)
        return@buildAnnotatedString
    }
    val idx = text.indexOf(query)
    if (idx > 0) append(text.substring(0, idx))
    withStyle(SpanStyle(background = HiFiColors.BrandSoft, color = HiFiColors.BrandDark)) {
        append(query)
    }
    val tail = idx + query.length
    if (tail < text.length) append(text.substring(tail))
}

private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    "UPDATE" -> JobKind.UPDATE
    "CLOSING" -> JobKind.CLOSING
    else -> JobKind.NEW
}

/** ISO8601 마감 → "6/15" 같은 짧은 표기 */
private fun displayDeadline(iso: String): String {
    // 단순 파싱: "2026-06-15T14:59:59Z" → "6/15"
    val date = iso.substringBefore('T')
    val parts = date.split('-')
    if (parts.size < 3) return ""
    val m = parts[1].trimStart('0')
    val d = parts[2].trimStart('0')
    return "$m/$d"
}

