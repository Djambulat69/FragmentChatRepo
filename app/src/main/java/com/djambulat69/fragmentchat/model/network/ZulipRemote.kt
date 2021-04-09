package com.djambulat69.fragmentchat.model.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory

private const val BASE_URL = "https://tfs-android-2021-spring.zulipchat.com/api/v1/"

private const val ANCHOR_MESSAGE_RANGE = 30
private const val ANCHOR_MESSAGE = "newest"
private const val MESSAGE_TYPE = "stream"

@ExperimentalSerializationApi
object ZulipRemote {

    private val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val zulipService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.createSynchronous())
        .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
        .client(client)
        .build()
        .create(ZulipChatService::class.java)

    fun getStreamsSingle() = zulipService.getStreams()

    fun getSubscriptionsSingle() = zulipService.getSubscriptions()

    fun getTopicsSingle(streamId: Int) = zulipService.getTopics(streamId)

    fun getTopicMessagesSingle(streamTitle: String, topicTitle: String) =
        zulipService.getMessages(
            ANCHOR_MESSAGE,
            ANCHOR_MESSAGE_RANGE,
            ANCHOR_MESSAGE_RANGE,
            Json.encodeToString(
                listOf(
                    NarrowSearchOperator("stream", streamTitle),
                    NarrowSearchOperator("topic", topicTitle)
                )
            )
        )

    fun getOwnUser() = zulipService.getOwnUser()

    fun getUsers() = zulipService.getUsers()

    fun sendMessageCompletable(streamId: Int, text: String, topicTitle: String) =
        zulipService.sendMessage(MESSAGE_TYPE, streamId, text, topicTitle)

    fun getStreamIdSingle(streamTitle: String) = zulipService.getStreamId(streamTitle)

    fun getUserPresence(idOrEmail: String) = zulipService.getUserPresence(idOrEmail)
}
