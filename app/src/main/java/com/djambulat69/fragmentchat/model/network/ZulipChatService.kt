package com.djambulat69.fragmentchat.model.network

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import okhttp3.Credentials
import retrofit2.http.*

const val myUserName = "Djambulat Isaev"

private const val API_KEY = "4nJfSYDBV23HhUcQgBxgQ3KIroRkMjDS"
private const val AUTH_HEADER = "Authorization"

private val credential: String = Credentials.basic("djambulat69@gmail.com", API_KEY)

interface ZulipChatService {

    @GET("streams")
    fun getStreams(@Header(AUTH_HEADER) cred: String = credential): Single<AllStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int, @Header(AUTH_HEADER) cred: String = credential): Single<TopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = false,
        @Header(AUTH_HEADER) cred: String = credential
    ): Single<MessagesResponse>

    @GET("users/me")
    fun getOwnUser(@Header(AUTH_HEADER) cred: String = credential): Single<OwnUser>

    @GET("users")
    fun getUsers(@Header(AUTH_HEADER) cred: String = credential): Single<AllUsersResponse>

    @POST("messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") streamId: Int,
        @Query("content") text: String,
        @Query("topic") topicTitle: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Maybe<SendMessageResponse>

    @GET("get_stream_id")
    fun getStreamId(
        @Query("stream") streamTitle: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Single<StreamIdResponse>

}
