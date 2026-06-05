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
 *  - 안드로이드 **에뮬레이터**에서 PC의 localhost는 `10.0.2.2`다 (localhost는 에뮬 자신).
 *  - **실기기**로 테스트하면 PC의 LAN IP(예: http://192.168.0.x:8080/)로 바꿔야 함.
 *  - 배포 시 실제 서버 도메인(https)로 교체. cleartext(http)는 dev 전용
 *    (AndroidManifest usesCleartextTraffic=true).
 */
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        ignoreUnknownKeys = true                       // 백엔드가 새 필드 추가해도 안 깨짐
        coerceInputValues = true                       // null이 non-null 자리에 와도 기본값으로
        namingStrategy = JsonNamingStrategy.SnakeCase  // camelCase(FE) ↔ snake_case(백엔드) 자동
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
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
