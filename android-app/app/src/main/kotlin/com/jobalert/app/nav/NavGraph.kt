package com.jobalert.app.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobalert.app.ui.components.HomeTab
import com.jobalert.app.ui.screens.detail.JobDetailScreen
import com.jobalert.app.ui.screens.main.MainScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingJobCategoryScreen

/**
 * 라우트 정의. 이번 세션 범위는 onb1 / main / detail/{id} 3개.
 * 나머지 23개 화면은 placeholder로 두고 다음 세션에 채움.
 */
object Routes {
    const val Onboarding1 = "onb1"
    const val Main = "main"
    const val Detail = "detail/{jobId}"
    fun detail(jobId: String) = "detail/$jobId"

    // Placeholders (스캐폴드만)
    const val Search = "search"
    const val Discover = "discover"
    const val Favorites = "favorites"
    const val Mypage = "mypage"
    const val Filter = "filter"
    const val NotifHistory = "notifHistory"
    const val ShareSheet = "share"
    const val Similar = "similar"
}

@Composable
fun JobAlertNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.Onboarding1) {
        composable(Routes.Onboarding1) {
            OnboardingJobCategoryScreen(
                onNext = { nav.navigate(Routes.Main) { popUpTo(Routes.Onboarding1) { inclusive = true } } },
                onSkip = { nav.navigate(Routes.Main) { popUpTo(Routes.Onboarding1) { inclusive = true } } },
            )
        }

        composable(Routes.Main) {
            MainScreen(
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
                onFilterClick = { nav.navigate(Routes.Filter) },
                onNotificationClick = { nav.navigate(Routes.NotifHistory) },
                onTabClick = { tab ->
                    when (tab) {
                        HomeTab.Home -> Unit
                        HomeTab.Search -> nav.navigate(Routes.Search)
                        HomeTab.Discover -> nav.navigate(Routes.Discover)
                        HomeTab.Favorites -> nav.navigate(Routes.Favorites)
                        HomeTab.Me -> nav.navigate(Routes.Mypage)
                    }
                },
            )
        }

        composable(
            route = Routes.Detail,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId").orEmpty()
            JobDetailScreen(
                jobId = jobId,
                onBack = { nav.popBackStack() },
                onShare = { nav.navigate(Routes.ShareSheet) },
                onSimilarTab = { nav.navigate(Routes.Similar) },
                onApply = { /* TODO: 외부 URL Intent */ },
            )
        }

        // 임시 placeholder들 — 다음 세션에서 실제 화면으로 교체
        listOf(
            Routes.Search to "검색",
            Routes.Discover to "찾아보기",
            Routes.Favorites to "관심기업",
            Routes.Mypage to "마이페이지",
            Routes.Filter to "필터",
            Routes.NotifHistory to "알림 히스토리",
            Routes.ShareSheet to "공유",
            Routes.Similar to "비슷한 공고",
        ).forEach { (route, label) ->
            composable(route) { Placeholder(label) }
        }
    }
}

@Composable
private fun Placeholder(label: String) {
    Text("$label 화면 (다음 세션 구현 예정)")
}
