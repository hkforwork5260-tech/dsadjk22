package com.jobalert.app.ui.screens.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jobalert.app.ui.components.*
import com.jobalert.app.ui.theme.HiFiColors
import com.jobalert.app.ui.theme.HiFiType

/**
 * 공유 시트. NavGraph 풀스크린 라우트지만 디자인은 BottomSheet.
 * 상단 dim 영역을 누르거나 "취소"를 누르면 닫힘.
 *
 * 공유 대상(제목·URL)은 호출한 화면이 nav 인자로 넘긴 실제 공고/회사 값.
 * 카카오톡: com.kakao.talk 패키지를 지정한 ACTION_SEND(설치돼 있으면 카톡으로 바로 공유).
 *   리치 템플릿(Kakao Link)은 SDK+앱키가 필요해 v0.2. 링크 복사는 클립보드.
 */
@Composable
fun ShareSheetScreen(
    shareTitle: String = "",
    shareUrl: String = "",
    onClose: () -> Unit,
) {
    val context = LocalContext.current

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClose,
            ),
    ) {
        // 하단 시트
        Box(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
                .background(HiFiColors.Bg)
                // 시트 내부 클릭은 dim 클릭을 막음
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {},
                )
                .padding(horizontal = 20.dp)
                .padding(top = 10.dp, bottom = 22.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // grabber
                Box(
                    Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(HiFiColors.Bg3),
                )
                Spacer(Modifier.height(14.dp))
                Text("공유", style = HiFiType.h2, color = HiFiColors.Text)
                if (shareTitle.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        shareTitle,
                        style = HiFiType.body2,
                        color = HiFiColors.Text2,
                        maxLines = 1,
                    )
                }
                Spacer(Modifier.height(18.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShareOption(
                        emoji = "💬",
                        label = "카카오톡",
                        bg = Color(0xFFFEE500),
                        onClick = {
                            shareToKakao(context, shareTitle, shareUrl)
                            onClose()
                        },
                    )
                    ShareOption(
                        emoji = "🔗",
                        label = "링크 복사",
                        bg = HiFiColors.Bg2,
                        onClick = {
                            copyToClipboard(context, shareUrl)
                            Toast.makeText(context, "링크를 복사했어요", Toast.LENGTH_SHORT).show()
                            onClose()
                        },
                    )
                }

                Spacer(Modifier.height(20.dp))
                HiFiButton(
                    text = "취소",
                    onClick = onClose,
                    variant = HiFiButtonVariant.Default,
                    size = HiFiButtonSize.Md,
                    fullWidth = true,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ShareOption(
    emoji: String,
    label: String,
    bg: Color,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .weight(1f)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            Text(emoji, style = HiFiType.h2.copy(fontSize = 28.sp))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            label,
            style = HiFiType.body2.copy(fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
            color = HiFiColors.Text,
        )
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("JobAlert link", text))
}

/** 카카오톡으로 바로 공유(설치돼 있으면). 텍스트+링크를 보낸다. 미설치 시 안내. */
private fun shareToKakao(context: Context, title: String, url: String) {
    val text = listOf(title, url).filter { it.isNotBlank() }.joinToString("\n")
        .ifBlank { "채용알리미에서 공고를 확인해보세요" }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
        setPackage("com.kakao.talk")
    }
    runCatching { context.startActivity(intent) }
        .onFailure {
            Toast.makeText(context, "카카오톡이 설치되어 있지 않아요", Toast.LENGTH_SHORT).show()
        }
}
