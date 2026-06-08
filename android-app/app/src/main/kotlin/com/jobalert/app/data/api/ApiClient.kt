package com.jobalert.app.data.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit 싱글톤. [ApiService] 구현체를 만들어 [api]로 노출한다.
 *
 * BASE_URL:
 *  - **운영(기본)**: Railway 클라우드 서버. 24시간 켜져 있어 에뮬·실기기 어디서나 동작.
 *  - 로컬 백엔드로 개발할 땐 아래 주석을 토글:
 *    - 에뮬레이터에서 PC의 localhost는 `10.0.2.2` (localhost는 에뮬 자신).
 *    - 실기기는 PC의 LAN IP(예: http://192.168.0.x:8080/).
 *  - cleartext(http)는 dev 전용(AndroidManifest usesCleartextTraffic=true). 운영은 https.
 */
object ApiClient {

    // 운영: Railway 클라우드. 로컬 개발 시 "http://10.0.2.2:8080/"로 교체.
    private const val BASE_URL = "https://dsadjk22-production.up.railway.app/"

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true                       // 백엔드가 새 필드 추가해도 안 깨짐
        coerceInputValues = true                       // null이 non-null 자리에 와도 기본값으로
        namingStrategy = JsonNamingStrategy.SnakeCase  // camelCase(FE) ↔ snake_case(백엔드) 자동
    }

    private val client = OkHttpClient.Builder()
        // 무료 박스 cold start(쉬다 깨어나는 첫 요청)가 느려 타임아웃 나던 것 → 넉넉히.
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        // 모든 요청에 익명 기기ID 헤더 — 관심기업 등 기기 기준 저장에 사용.
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .header("X-Device-Id", DeviceId.value)
                .build()
            chain.proceed(req)
        }
        .addInterceptor(
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC },
        )
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}
