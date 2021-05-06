package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.http.*

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
    fun getOwnUser(): Single<User>

    @GET("users")
    fun getUsers(): Single<AllUsersResponse>

    @GET("users/{user_id_or_email}/presence")
    fun getUserPresence(
        @Path("user_id_or_email") idOrEmail: String
    ): Single<UserPresenceResponse>

    @GET("events")
    fun getEventsQueue(
        @Query("queue_id") queueId: String,
        @Query("last_event_id") lastEventId: Int
    ): Single<GetEventsResponse>


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

    @POST("users/me/subscriptions")
    fun subscribeOnStreams(
        @Query("subscriptions") subscribtions: String,
        @Query("inviteOnly") inviteOnly: Boolean
    ): Completable

    @POST("register")
    fun registerEventsQueue(
        @Query("event_types") events: String = Json.encodeToString(arrayOf("message"))
    ): Single<RegisterEventResponse>


    @DELETE("messages/{message_id}/reactions")
    fun deleteReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") emojiName: String
    ): Completable


    @PATCH("messages/{msgId}")
    fun editMessageText(
        @Path("msgId") id: Int,
        @Query("content") newText: String
    ): Completable

}
