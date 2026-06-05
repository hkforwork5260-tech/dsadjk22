package com.jobalert.app.ui.screens.filter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 현재 적용된 직군 필터를 보관하는 전역 상태 홀더.
 *
 * 필터 화면(별도 라우트)에서 고른 직군을 메인 피드로 전달하는 가장 단순·확실한 통로.
 * Compose State라 메인 화면이 [categories]를 읽으면 자동 구독 → 변경 시 재조회된다.
 * (v0.1 단일 사용자·단일 프로세스 전제. 다중 필터 facet·영속화는 추후.)
 */
object ActiveFilter {
    /** 적용된 직군 코드들. 빈 리스트면 전체. */
    var categories by mutableStateOf<List<String>>(emptyList())
}
