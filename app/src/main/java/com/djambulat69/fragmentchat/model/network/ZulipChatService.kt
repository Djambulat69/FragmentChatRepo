package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

const val myUserId = 402250

interface ZulipChatService {

    @GET("streams")
    fun getStreams(): Single<StreamsResponseSealed.AllStreamsResponse>

    @GET("users/me/subscriptions")
    fun getSubscriptions(): Single<StreamsResponseSealed.SubscribedStreamsResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopics(@Path("stream_id") streamId: Int): Single<TopicsResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: Long,
        @Query("num_before") numBefore: Int,
        @Query("num_after") numAfter: Int,
        @Query("narrow") narrow: String,
        @Query("apply_markdown") applyMarkdown: Boolean = false
    ): Single<MessagesResponse>

    @GET("users/me")
    fun getOwnUser(): Single<OwnUser>

    @GET("users")
    fun getUsers(): Single<AllUsersResponse>

    @GET("users/{user_id_or_email}/presence")
    fun getUserPresence(
        @Path("user_id_or_email") idOrEmail: String
    ): Single<UserPresenceResponse>


    @POST("messages")
    fun sendMessage(
        @Query("type") type: String,
        @Query("to") streamId: Int,
        @Query("content") text: String,
        @Query("topic") topicTitle: String
    ): Completable

    @POST("messages/{message_id}/reactions")
    fun addReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Completable


    @DELETE("messages/{message_id}/reactions")
    fun deleteReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Completable

}
