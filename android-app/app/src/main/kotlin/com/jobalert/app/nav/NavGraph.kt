package com.jobalert.app.nav

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jobalert.app.ui.components.HomeTab
import com.jobalert.app.ui.screens.calendar.CalendarScreen
import com.jobalert.app.ui.screens.company.CompanyDetailScreen
import com.jobalert.app.ui.screens.detail.JobDetailScreen
import com.jobalert.app.ui.screens.discover.DiscoverScreen
import com.jobalert.app.ui.screens.favorites.FavoritesScreen
import com.jobalert.app.data.model.JobCategoryCodes
import com.jobalert.app.ui.screens.filter.ActiveFilter
import com.jobalert.app.ui.screens.filter.FilterScreen
import com.jobalert.app.ui.screens.main.MainEmptyScreen
import com.jobalert.app.ui.screens.main.MainScreen
import com.jobalert.app.ui.screens.mypage.MyPageScreen
import com.jobalert.app.ui.screens.notif.NotifHistoryScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingCompanySizeScreen
import com.jobalert.app.ui.screens.search.SearchResultsScreen
import com.jobalert.app.ui.screens.search.SearchScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingCompanySwipeScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingJobCategoryScreen
import com.jobalert.app.ui.screens.onboarding.OnboardingWidgetScreen
import com.jobalert.app.ui.screens.settings.FeedbackScreen
import com.jobalert.app.ui.screens.settings.InterestsScreen
import com.jobalert.app.ui.screens.settings.NotifSettingsScreen
import com.jobalert.app.ui.screens.settings.WidgetSettingsScreen
import com.jobalert.app.ui.screens.share.ShareSheetScreen
import com.jobalert.app.ui.screens.similar.SimilarJobsScreen

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

    const val Discover = "discover"
    const val Favorites = "favorites"
    const val Mypage = "mypage"
    const val Filter = "filter"
    const val NotifHistory = "notifHistory"
    const val Calendar = "calendar"
    const val ShareSheet = "share"
    const val Similar = "similar/{jobId}"
    fun similar(jobId: String) = "similar/$jobId"

    // 마이페이지 서브
    const val NotifSettings = "notifSettings"
    const val WidgetSettings = "widgetSettings"
    const val Interests = "interests"
    const val Feedback = "feedback"
}

@Composable
fun JobAlertNavHost() {
    val nav = rememberNavController()
    val context = LocalContext.current

    // 지원하기 → 원본 채용 URL을 브라우저로 열기.
    fun openUrl(url: String) {
        if (url.isBlank()) return
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    }

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
                onSimilarTab = { nav.navigate(Routes.similar(jobId)) },
                onCompanyClick = { cid -> nav.navigate(Routes.company(cid)) },
                onApply = { job -> openUrl(job.originalUrl) },
            )
        }

        composable(Routes.Filter) {
            FilterScreen(
                onClose = { nav.popBackStack() },
                onApply = { sel ->
                    // 선택 → 백엔드 필터값으로 변환. 메인이 ActiveFilter를 구독해 재조회.
                    // (지역·마감 facet은 데이터 형식 편차로 v0.1 미적용)
                    val categories = sel.jobs.mapNotNull { JobCategoryCodes.getOrNull(it) }
                    val experiences = when (sel.experience) {
                        "신입" -> listOf("신입")
                        "", "무관" -> emptyList()
                        else -> listOf("경력")   // 1~3년/3~5년/5년+ → 경력
                    }
                    val sizes = sel.sizes.mapNotNull { sizeCode(it) }
                    ActiveFilter.set(categories = categories, experiences = experiences, sizes = sizes)
                    nav.popBackStack()
                },
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
                onNotifSettings = { nav.navigate(Routes.NotifSettings) },
                onWidgetSettings = { nav.navigate(Routes.WidgetSettings) },
                onInterests = { nav.navigate(Routes.Interests) },
                onFeedback = { nav.navigate(Routes.Feedback) },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.Mypage) },
            )
        }

        composable(Routes.NotifSettings) {
            NotifSettingsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.WidgetSettings) {
            WidgetSettingsScreen(onBack = { nav.popBackStack() })
        }
        composable(Routes.Interests) {
            InterestsScreen(
                onBack = { nav.popBackStack() },
                onEditJobCategory = { nav.navigate(Routes.Onboarding1) },
                onEditCompanySize = { nav.navigate(Routes.Onboarding2) },
                onEditCompanySwipe = { nav.navigate(Routes.Onboarding3) },
                onOpenFavorites = { nav.navigate(Routes.Favorites) },
            )
        }
        composable(Routes.Feedback) {
            FeedbackScreen(onBack = { nav.popBackStack() })
        }

        composable(Routes.NotifHistory) {
            NotifHistoryScreen(
                onBack = { nav.popBackStack() },
                onItemClick = { n ->
                    // 첫 jobId 있으면 공고 상세, 없으면 main으로
                    val firstJob = n.jobIds.firstOrNull()
                    if (firstJob != null) nav.navigate(Routes.detail(firstJob))
                    else nav.popBackStack()
                },
            )
        }

        composable(Routes.Calendar) {
            CalendarScreen(
                onBack = { nav.popBackStack() },
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
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

        composable(Routes.ShareSheet) {
            ShareSheetScreen(onClose = { nav.popBackStack() })
        }

        composable(
            route = Routes.Similar,
            arguments = listOf(navArgument("jobId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val jId = backStackEntry.arguments?.getString("jobId").orEmpty()
            SimilarJobsScreen(
                jobId = jId,
                onBack = { nav.popBackStack() },
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
            )
        }

        composable(Routes.Discover) {
            DiscoverScreen(
                onJobClick = { id -> nav.navigate(Routes.detail(id)) },
                onShare = { nav.navigate(Routes.ShareSheet) },
                onFilter = { nav.navigate(Routes.Filter) },
                onGoMain = { goMain(nav) },
                onTabClick = { tab -> handleTab(nav, tab, currentRoute = Routes.Discover) },
            )
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

/** 필터 화면의 한글 규모 라벨 → 백엔드 size 코드. 모르면 null(필터에서 제외). */
private fun sizeCode(label: String): String? = when (label) {
    "대기업" -> "large_corp"
    "공기업" -> "public"
    "중견" -> "mid_corp"
    "중소" -> "sme"
    "외국계" -> "foreign"
    "스타트업" -> "startup_unicorn"
    else -> null
}

