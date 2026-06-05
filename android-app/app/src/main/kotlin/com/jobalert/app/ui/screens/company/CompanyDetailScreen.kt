package com.jobalert.app.ui.screens.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Share
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jobalert.app.data.api.CompanyDto
import com.jobalert.app.data.api.CompanyDetailResponse
import com.jobalert.app.data.api.CompanyStats
import com.jobalert.app.data.api.JobHistoryDto
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.*

/**
 * 회사 상세 화면.
 * HiFi_CompanyPage 대응. 공고 있음/없음 두 케이스를 같은 컴포넌트로 처리.
 *
 * - postings.isNotEmpty(): 진행중인 공고 리스트
 * - postings.isEmpty(): "지금은 채용 공고가 없어요" 카드 + 최근 채용 이력
 */
@Composable
fun CompanyDetailScreen(
    companyId: Int,
    onBack: () -> Unit,
    onJobClick: (String) -> Unit,
    onShare: () -> Unit,
) {
    // 백엔드 /companies/{id}/page 연결. 로딩·에러 시엔 빈 회사 페이지로 렌더.
    val viewModel: CompanyDetailViewModel = viewModel()
    LaunchedEffect(companyId) { viewModel.load(companyId) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val data = (state as? CompanyUiState.Success)?.data ?: emptyCompanyPage(companyId)
    var starred by remember(companyId) { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(HiFiColors.Bg)) {
        HiFiStatusBar()
        HiFiAppBar(
            title = "",
            leading = { HiFiIconBtn(Icons.Outlined.ArrowBack, "뒤로", onClick = onBack) },
            action = { HiFiIconBtn(Icons.Outlined.Share, "공유", onClick = onShare) },
        )

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            // 헤더 (로고 + 회사명 + 산업)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(HiFiColors.BrandSoft),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        data.company.logo,
                        style = HiFiType.title.copy(fontSize = 24.sp),
                        color = HiFiColors.BrandDark,
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    data.company.name,
                    style = HiFiType.display.copy(fontSize = 24.sp),
                    color = HiFiColors.Text,
                )
                Spacer(Modifier.height(4.dp))
                Text(data.company.industry, style = HiFiType.body2, color = HiFiColors.Text2)

                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    HiFiChip(text = sizeLabel(data.company.size), variant = HiFiChipVariant.Outline, small = true)
                    HiFiChip(text = "📍 ${data.region}", variant = HiFiChipVariant.Outline, small = true)
                }

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HiFiButton(
                        text = if (starred) "✓ 관심기업" else "+ 관심기업",
                        onClick = { starred = !starred },
                        variant = if (starred) HiFiButtonVariant.Primary else HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                    )
                    HiFiButton(
                        text = "🔗 홈페이지",
                        onClick = { /* TODO: 외부 URL */ },
                        variant = HiFiButtonVariant.Default,
                        size = HiFiButtonSize.Sm,
                    )
                }
            }

            // 회사 소개 카드
            Box(
                Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, HiFiColors.Border, RoundedCornerShape(16.dp))
                    .padding(16.dp),
            ) {
                Column {
                    Text("회사 소개", style = HiFiType.caption, color = HiFiColors.Text2)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        data.about,
                        style = HiFiType.body.copy(lineHeight = 21.sp),
                        color = HiFiColors.Text,
                    )
                }
            }

            // 통계 3개 (올해 신규 / 평균 마감 / 합격률)
            Row(
                Modifier.padding(horizontal = 20.dp, vertical = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatBox(label = "올해 신규", value = "${data.stats.thisYearCount}건", color = HiFiColors.Brand, modifier = Modifier.weight(1f))
                StatBox(label = "평균 마감", value = data.stats.avgCloseLabel, color = HiFiColors.Text, modifier = Modifier.weight(1f))
                StatBox(label = "합격률", value = data.stats.passRateLabel, color = HiFiColors.NewShadow, modifier = Modifier.weight(1f))
            }

            // 진행중인 공고 헤더
            Row(
                Modifier.padding(horizontal = 20.dp).padding(top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("진행중인 공고", style = HiFiType.h2, color = HiFiColors.Text, modifier = Modifier.weight(1f))
                if (data.postings.isNotEmpty()) {
                    Text(
                        "${data.postings.size}건",
                        style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp),
                        color = HiFiColors.Text2,
                    )
                }
            }

            if (data.postings.isNotEmpty()) {
                Column(
                    Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    data.postings.forEach { job ->
                        HiFiJobCard(
                            kind = kindOf(job.kind),
                            company = job.company.name,
                            role = job.title,
                            logo = job.company.logo,
                            dday = job.dday,
                            dateText = "",
                            onClick = { onJobClick(job.id) },
                        )
                    }
                }
            } else {
                EmptyPostingsCard(starred = starred, modifier = Modifier.padding(horizontal = 20.dp))
                Text(
                    "최근 채용 이력",
                    style = HiFiType.h2,
                    color = HiFiColors.Text,
                    modifier = Modifier.padding(horizontal = 20.dp).padding(top = 22.dp, bottom = 10.dp),
                )
                Column(
                    Modifier.padding(horizontal = 20.dp).padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    data.history.forEach { h ->
                        HistoryRow(h)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }

        HiFiGestureNav()
    }
}

/** 로딩·에러 중 화면이 깨지지 않게 쓰는 빈 회사 페이지. */
private fun emptyCompanyPage(id: Int) = CompanyDetailResponse(
    company = CompanyDto(id = id, name = "", logo = ""),
    region = "",
    about = "",
    stats = CompanyStats(thisYearCount = 0, avgCloseLabel = "—", passRateLabel = "—"),
    postings = emptyList(),
    history = emptyList(),
)

@Composable
private fun StatBox(label: String, value: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(RoundedCornerShape(14.dp))
            .background(HiFiColors.Bg2)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, style = HiFiType.body2.copy(fontSize = 11.sp), color = HiFiColors.Text2)
            Spacer(Modifier.height(2.dp))
            Text(value, style = HiFiType.monoNum.copy(fontSize = 22.sp), color = color)
        }
    }
}

@Composable
private fun EmptyPostingsCard(starred: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(androidx.compose.ui.graphics.Color.Transparent)
            .border(2.dp, HiFiColors.BorderDark, RoundedCornerShape(16.dp))
            .padding(22.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Mascot(size = 64.dp, expression = MascotExpression.Sleep)
            Spacer(Modifier.height(8.dp))
            Text("지금은 채용 공고가 없어요", style = HiFiType.h2, color = HiFiColors.Text)
            Spacer(Modifier.height(6.dp))
            Text(
                buildString {
                    append("새 공고가 뜨면 알려드릴게요.")
                    if (starred) append("\n관심기업으로 등록되어 있어요 ✓")
                },
                style = HiFiType.body2.copy(lineHeight = 19.sp),
                color = HiFiColors.Text2,
            )
            Spacer(Modifier.height(14.dp))
            HiFiButton(
                text = "🔗 채용 사이트 직접 보기",
                onClick = { /* TODO 외부 URL */ },
                variant = HiFiButtonVariant.Default,
                size = HiFiButtonSize.Sm,
            )
        }
    }
}

@Composable
private fun HistoryRow(h: JobHistoryDto) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HiFiColors.Bg2)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(h.role, style = HiFiType.body.copy(fontWeight = FontWeight.Bold), color = HiFiColors.Text)
            Text(h.period, style = HiFiType.body2.copy(fontSize = 12.sp), color = HiFiColors.Text2)
        }
        Text("마감", style = HiFiType.body2.copy(fontWeight = FontWeight.Bold, fontSize = 12.sp), color = HiFiColors.Text2)
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

private fun kindOf(s: String): JobKind = when (s.uppercase()) {
    "NEW" -> JobKind.NEW
    "UPDATE" -> JobKind.UPDATE
    "CLOSING" -> JobKind.CLOSING
    else -> JobKind.NEW
}

