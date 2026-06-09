package com.jobalert.app.ui.screens.filter

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jobalert.app.data.fcm.FcmRegistrar

/**
 * 현재 적용된 필터(직군·경력·규모)를 보관하는 전역 상태 홀더 + 로컬 영속.
 *
 * 필터 화면·온보딩에서 고른 조건을 메인 피드로 전달하는 통로. Compose State라 메인이 읽으면
 * 자동 구독 → 변경 시 재조회. SharedPreferences 영속(앱 재시작 유지).
 * MainActivity.onCreate에서 [init] 먼저 호출.
 */
object ActiveFilter {
    private const val PREFS = "jobalert_prefs"
    // prefs 키 문자열은 기존 저장값 호환 위해 유지(이름만 관심으로 의미 변경).
    private const val KEY_CAT = "filter_categories"
    private const val KEY_SIZE = "filter_sizes"
    private const val KEY_ONBOARDED = "onboarding_done"
    // 관심을 마지막으로 설정/변경한 날(KST epochDay). 이 날 = 조건 맞는 공고 '전체' 노출,
    // 다음날부터 = '전날 대비 신규(kind=NEW)'만. 공고별 기록이 아니라 날짜 하나만 저장.
    private const val KEY_INTEREST_DATE = "interest_set_epochday"

    private var appContext: Context? = null

    /** 온보딩 완료 여부(처음 설치 때 1회만 보이게). 시작 화면 결정에 사용. */
    var onboardingDone: Boolean = false
        private set

    // ── 관심(온보딩·내정보) : 영속. 푸시 개인화·피드 기본 조건의 원천. ──
    /** 관심 직군 코드들(영속). 빈 리스트면 전체. */
    var interestCategories by mutableStateOf<List<String>>(emptyList()); private set
    /** 관심 회사 규모 코드들(영속). 빈 리스트면 전체. */
    var interestSizes by mutableStateOf<List<String>>(emptyList()); private set

    // ── 세션 필터(필터 다이얼로그) : 비영속. 피드가 실제로 쓰는 값. 시작 시 관심으로 초기화. ──
    /** 직군 코드들. 빈 리스트면 전체. */
    var categories by mutableStateOf<List<String>>(emptyList()); private set
    /** 경력 버킷(신입/경력). 빈 리스트면 전체. */
    var experiences by mutableStateOf<List<String>>(emptyList()); private set
    /** 회사 규모 코드(large_corp/public/…). 빈 리스트면 전체. */
    var sizes by mutableStateOf<List<String>>(emptyList()); private set
    /** 마감일 필터: N일 이내 마감만(0=오늘·3=D-3…). -1이면 전체(필터 없음). */
    var deadlineDays by mutableStateOf(-1); private set

    /** 관심 마지막 설정일(KST epochDay). -1이면 미설정. */
    var interestSetEpochDay by mutableStateOf(-1L); private set
    /** 세션 필터가 적용된 상태인지(일회성). 필터 적용 동안은 '전체' 노출. init/관심설정 시 false. */
    var filterActive by mutableStateOf(false); private set

    fun init(context: Context) {
        appContext = context.applicationContext
        interestCategories = load(KEY_CAT)
        interestSizes = load(KEY_SIZE)
        // 세션 필터는 관심으로 시작(경력·마감은 필터 전용이라 비움).
        categories = interestCategories
        sizes = interestSizes
        experiences = emptyList()
        deadlineDays = -1
        filterActive = false
        interestSetEpochDay = prefs()?.getLong(KEY_INTEREST_DATE, -1L) ?: -1L
        onboardingDone = prefs()?.getBoolean(KEY_ONBOARDED, false) ?: false
    }

    /** 오늘(KST) epochDay. java.time 없이 millis로 계산(안드 구버전 호환). */
    private fun todayEpochDayKst(): Long =
        (System.currentTimeMillis() + 9L * 3600 * 1000) / (24L * 3600 * 1000)

    /**
     * 오늘 '전체 노출' 모드인가 — 관심을 오늘 설정했거나(첫설치/관심변경 당일) 필터가 걸린 상태.
     * true면 오늘 탭 NEW = 조건 맞는 공고 전체, false면 = 전날 대비 신규(kind=NEW)만.
     */
    fun showAllToday(): Boolean = filterActive || (interestSetEpochDay == todayEpochDayKst())

    /** 온보딩 1회 완료 표시(영속). 이후 앱 시작 시 메인으로 바로 진입. */
    fun markOnboardingDone() {
        onboardingDone = true
        prefs()?.edit()?.putBoolean(KEY_ONBOARDED, true)?.apply()
    }

    /**
     * 관심(온보딩·내정보) 설정 — 영속 + 푸시 재동기화. 피드(세션)의 직군·규모도 새 관심으로 맞춘다.
     * (필터 다이얼로그의 '일회성' 선택과 달리 기억되는 값.) 미지정 인자는 현재값 유지.
     */
    fun setInterest(
        categories: List<String> = interestCategories,
        sizes: List<String> = interestSizes,
    ) {
        interestCategories = categories
        interestSizes = sizes
        save(KEY_CAT, categories)
        save(KEY_SIZE, sizes)
        // 관심을 바꾼 '오늘'을 설정일로 기록 → 오늘은 전체 노출, 내일부터 신규만. 필터 모드는 해제.
        interestSetEpochDay = todayEpochDayKst()
        prefs()?.edit()?.putLong(KEY_INTEREST_DATE, interestSetEpochDay)?.apply()
        filterActive = false
        // 필터를 따로 안 걸었다면 피드가 관심대로 보이도록 세션도 갱신.
        this.categories = categories
        this.sizes = sizes
        FcmRegistrar.refresh(categories, sizes)
    }

    /** 필터 다이얼로그 적용 — 세션만(비영속). 관심은 바뀌지 않는다(일회성). */
    fun setFilter(
        categories: List<String>,
        experiences: List<String>,
        sizes: List<String>,
        deadlineDays: Int,
    ) {
        this.categories = categories
        this.experiences = experiences
        this.sizes = sizes
        this.deadlineDays = deadlineDays
        // 필터 적용 동안은 그 조건에 맞는 공고 '전체' 노출(일회성). 앱 재시작 시 init에서 해제.
        filterActive = true
    }

    private fun load(key: String): List<String> =
        prefs()?.getString(key, null)?.split(",")?.filter { it.isNotBlank() } ?: emptyList()

    private fun save(key: String, values: List<String>) {
        prefs()?.edit()?.putString(key, values.joinToString(","))?.apply()
    }

    private fun prefs() = appContext?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
