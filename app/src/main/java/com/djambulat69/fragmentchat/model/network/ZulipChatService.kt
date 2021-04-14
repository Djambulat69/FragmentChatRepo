package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.Credentials
import retrofit2.http.*

const val myUserId = 402250

private const val API_KEY = "4nJfSYDBV23HhUcQgBxgQ3KIroRkMjDS"
private const val AUTH_HEADER = "Authorization"

private val credential: String = Credentials.basic("djambulat69@gmail.com", API_KEY)


interface ZulipChatService {

    @GET("streams")
    fun getStreams(@Header(AUTH_HEADER) cred: String = credential): Single<StreamsResponseSealed.AllStreamsResponse>

    @GET("users/me/subscriptions")
    fun getSubscriptions(@Header(AUTH_HEADER) cred: String = credential): Single<StreamsResponseSealed.SubscribedStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int, @Header(AUTH_HEADER) cred: String = credential): Single<TopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: Long,
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

    @GET("users/{user_id_or_email}/presence")
    fun getUserPresence(
        @Path("user_id_or_email") idOrEmail: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Single<UserPresenceResponse>


    @POST("messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") streamId: Int,
        @Query("content") text: String,
        @Query("topic") topicTitle: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Completable

    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Completable


    @DELETE("messages/{message_id}/reactions")
    fun deleteReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String,
        @Header(AUTH_HEADER) cred: String = credential
    ): Completable

}
