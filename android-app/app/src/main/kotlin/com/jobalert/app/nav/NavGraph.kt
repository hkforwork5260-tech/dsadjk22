package com.jobalert.app.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobalert.app.ui.components.HomeTab
import com.jobalert.app.ui.screens.company.CompanyDetailScreen
import com.jobalert.app.ui.screens.detail.JobDetailScreen
import com.jobalert.app.ui.screens.favorites.FavoritesScreen
import com.jobalert.app.ui.screens.filter.FilterScreen
import com.jobalert.app.ui.screens.main.MainEmptyScreen
import com.jobalert.app.ui.screens.main.MainScreen
import com.jobalert.app.ui.screens.mypage.MyPageScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingCompanySizeScreen
import com.jobalert.app.ui.screens.search.SearchResultsScreen
import com.jobalert.app.ui.screens.search.SearchScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingCompanySwipeScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingJobCategoryScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingWidgetScreen

/**
 * 라우트 정의.
 * 온보딩 1→2→3→4 → main. 각 단계에서 건너뛰기 시 바로 main.
 */
object Routes {
    const val Onboarding1 = "onb1"
    const val Onboarding2 = "onb2"
    const val Onboarding3 = "onb3"
    const val Onboarding4 = "onb4"
    const val Main = "main"
    const val MainEmpty = "mainEmpty"
    const val Detail = "detail/{jobId}"
    fun detail(jobId: String) = "detail/$jobId"

    const val CompanyDetail = "company/{companyId}"
    fun company(id: Int) = "company/$id"

    const val Search = "search"
    const val SearchResults = "searchResults?q={q}"
    fun searchResults(q: String) = "searchResults?q=$q"

    // Placeholders (다음 세션에서 구현)
    const val Discover = "discover"
    const val Favorites = "favorites"
    const val Mypage = "mypage"
    const val Filter = "filter"
    const val NotifHistory = "notifHistory"
    const val Calendar = "calendar"
    const val ShareSheet = "share"
    const val Similar = "similar"
}

@Composable
fun JobAlertNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Routes.Onboarding1) {
        composable(Routes.Onboarding1) {
            OnboardingJobCategoryScreen(
                onNext = { nav.navigate(Routes.Onboarding2) },
                onSkip = { goMain(nav) },
            )
        }

        composable(Routes.Onboarding2) {
            OnboardingCompanySizeScreen(
                onNext = { nav.navigate(Routes.Onboarding3) },
                onSkip = { goMain(nav) },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.Onboarding3) {
            OnboardingCompanySwipeScreen(
                onNext = { nav.navigate(Routes.Onboarding4) },
                onSkip = { goMain(nav) },
                onBack = { nav.popBackStack() },
            )
        }

        composable(Routes.Onboarding4) {
            OnboardingWidgetScreen(
                onAllow = { goMain(nav) },
                onLater = { goMain(nav) },
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

        composable(Routes.MainEmpty) {
            MainEmptyScreen(
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
                onFilterClick = { nav.navigate(Routes.Filter) },
                onNotificationClick = { nav.navigate(Routes.NotifHistory) },
                onAddFavorites = { nav.navigate(Routes.Favorites) },
                onTabClick = { tab ->
                    when (tab) {
                        HomeTab.Home -> nav.navigate(Routes.Main) {
                            popUpTo(Routes.MainEmpty) { inclusive = true }
                        }
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

        composable(Routes.Filter) {
            FilterScreen(
                onClose = { nav.popBackStack() },
                onApply = { _ -> nav.popBackStack() },
            )
        }

        composable(
            route = Routes.CompanyDetail,
            arguments = listOf(navArgument("companyId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("companyId") ?: 1
            CompanyDetailScreen(
                companyId = id,
                onBack = { nav.popBackStack() },
                onJobClick = { jid -> nav.navigate(Routes.detail(jid)) },
                onShare = { nav.navigate(Routes.ShareSheet) },
            )
        }

        composable(Routes.Favorites) {
            FavoritesScreen(
                onCompanyClick = { cid -> nav.navigate(Routes.company(cid)) },
                onAddCompany = { /* TODO: 검색으로 이동 또는 add flow */ },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.Favorites) },
            )
        }

        composable(Routes.Mypage) {
            MyPageScreen(
                onNotifHistory = { nav.navigate(Routes.NotifHistory) },
                onCalendar = { nav.navigate(Routes.Calendar) },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.Mypage) },
            )
        }

        composable(Routes.Search) {
            SearchScreen(
                onSearch = { q -> nav.navigate(Routes.searchResults(q)) },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.Search) },
            )
        }

        composable(
            route = Routes.SearchResults,
            arguments = listOf(navArgument("q") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }),
        ) { backStackEntry ->
            val q = backStackEntry.arguments?.getString("q").orEmpty()
            SearchResultsScreen(
                query = q,
                onBack = { nav.popBackStack() },
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
                onCompanyClick = { cid -> nav.navigate(Routes.company(cid)) },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.SearchResults) },
            )
        }

        // 임시 placeholder들 — 다음 세션에서 실제 화면으로 교체
        listOf(
            Routes.Discover to "찾아보기",
            Routes.NotifHistory to "알림 히스토리",
            Routes.Calendar to "마감 캘린더",
            Routes.ShareSheet to "공유",
            Routes.Similar to "비슷한 공고",
        ).forEach { (route, label) ->
            composable(route) { Placeholder(label) }
        }
    }
}

private fun handleTab(
    nav: androidx.navigation.NavHostController,
    tab: HomeTab,
    currentRoute: String,
) {
    val target = when (tab) {
        HomeTab.Home -> Routes.Main
        HomeTab.Search -> Routes.Search
        HomeTab.Discover -> Routes.Discover
        HomeTab.Favorites -> Routes.Favorites
        HomeTab.Me -> Routes.Mypage
    }
    if (target == currentRoute) return
    nav.navigate(target) { launchSingleTop = true }
}

private fun goMain(nav: androidx.navigation.NavHostController) {
    nav.navigate(Routes.Main) {
        popUpTo(Routes.Onboarding1) { inclusive = true }
        launchSingleTop = true
    }
}

@Composable
private fun Placeholder(label: String) {
    Text("$label 화면 (다음 세션 구현 예정)")
}
