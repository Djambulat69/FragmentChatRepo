package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val MESSAGE_TYPE = "stream"
private const val MESSAGES_COUNT_AFTER_ANCHOR = 0

object ZulipServiceImpl {

    private val zulipService: ZulipChatService = ZulipRetrofit.get()

    fun getStreamsSingle(): Single<StreamsResponseSealed.AllStreamsResponse> = zulipService.getStreams()

    fun getSubscriptionsSingle(): Single<StreamsResponseSealed.SubscribedStreamsResponse> = zulipService.getSubscriptions()

    fun getTopicsSingle(streamId: Int): Single<TopicsResponse> = zulipService.getTopics(streamId)

    fun getTopicMessagesSingle(streamTitle: String, topicTitle: String, anchor: Long, count: Int): Single<MessagesResponse> =
        zulipService.getMessages(
            anchor,
            count,
            MESSAGES_COUNT_AFTER_ANCHOR,
            Json.encodeToString(
                listOf(
                    NarrowSearchOperator("stream", streamTitle),
                    NarrowSearchOperator("topic", topicTitle)
                )
            )
        )

    fun getOwnUser(): Single<OwnUser> = zulipService.getOwnUser()

    fun getUsers(): Single<AllUsersResponse> = zulipService.getUsers()

    fun sendMessageCompletable(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessage(MESSAGE_TYPE, streamId, text, topicTitle)

    fun getUserPresence(idOrEmail: String): Single<UserPresenceResponse> = zulipService.getUserPresence(idOrEmail)

    fun addReaction(messageId: Int, emojiName: String): Completable = zulipService.addReaction(messageId, emojiName)

    fun deleteReaction(messageId: Int, emojiName: String): Completable = zulipService.deleteReaction(messageId, emojiName)
}
