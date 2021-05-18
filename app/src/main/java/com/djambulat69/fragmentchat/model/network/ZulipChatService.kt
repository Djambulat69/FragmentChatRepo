package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import okhttp3.MultipartBody
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
        @Query("apply_markdown") applyMarkdown: Boolean = true
    ): Single<MessagesResponse>

    @GET("users/me")
    fun getOwnUser(): Single<User>

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

    @POST("users/me/subscriptions")
    fun subscribeOnStreams(
        @Query("subscriptions") subscriptions: String,
        @Query("inviteOnly") inviteOnly: Boolean
    ): Completable

    @POST("mark_stream_as_read")
    fun markStreamAsRead(@Query("stream_id") id: Int): Completable

    @POST("mark_topic_as_read")
    fun markTopicAsRead(
        @Query("stream_id") streamId: Int,
        @Query("topic_name") topicTitle: String
    ): Completable

    @Multipart
    @POST("user_uploads")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Single<FileResponse>


    @DELETE("messages/{msg_id}")
    fun deleteMessage(
        @Path("msg_id") id: Int
    ): Completable

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

    @PATCH("messages/{msgId}")
    fun changeMessageTopic(
        @Path("msgId") id: Int,
        @Query("topic") newTopic: String
    ): Completable

}
