package com.djambulat69.fragmentchat.model.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.util.concurrent.TimeUnit

object ZulipRetrofit {

    private const val BASE_URL = "https://tfs-android-2021-spring.zulipchat.com/api/v1/"
    private const val READ_TIMEOUT_MILLIS = 0L

    private val client: OkHttpClient
        get() {
            val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val authInterceptor = AuthInterceptor()
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build()
        }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .client(client)
        .build()
        .create(ZulipChatService::class.java)

    fun get(): ZulipChatService = retrofit

}
