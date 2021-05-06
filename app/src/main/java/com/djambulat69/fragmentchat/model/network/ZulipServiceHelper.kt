package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val MESSAGE_TYPE = "stream"
private const val MESSAGES_COUNT_AFTER_ANCHOR = 0

class ZulipServiceHelper @Inject constructor(private val zulipService: ZulipChatService) {

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
                    NarrowSearchOperator(NarrowSearchOperator.STREAM_OPERATOR, streamTitle),
                    NarrowSearchOperator(NarrowSearchOperator.TOPIC_OPERATOR, topicTitle)
                )
            )
        )

    fun getStreamMessagesSingle(streamTitle: String, anchor: Long, count: Int): Single<MessagesResponse> =
        zulipService.getMessages(
            anchor,
            count,
            MESSAGES_COUNT_AFTER_ANCHOR,
            Json.encodeToString(
                listOf(
                    NarrowSearchOperator(NarrowSearchOperator.STREAM_OPERATOR, streamTitle)
                )
            )
        )

    fun getOwnUser(): Single<User> = zulipService.getOwnUser()

    fun getUsers(): Single<AllUsersResponse> = zulipService.getUsers()

    fun sendMessageCompletable(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessage(MESSAGE_TYPE, streamId, text, topicTitle)

    fun getUserPresence(idOrEmail: String): Single<UserPresenceResponse> = zulipService.getUserPresence(idOrEmail)

    fun addReaction(messageId: Int, emojiName: String): Completable = zulipService.addReaction(messageId, emojiName)

    fun deleteReaction(messageId: Int, emojiName: String): Completable = zulipService.deleteReaction(messageId, emojiName)

    fun subscribeOnStream(subscribtion: Subscribtion, inviteOnly: Boolean): Completable =
        zulipService.subscribeOnStreams(
            Json.encodeToString(listOf(subscribtion)),
            inviteOnly
        )

    fun editMessageText(id: Int, newText: String): Completable = zulipService.editMessageText(id, newText)

    fun registerEventQueue(): Single<RegisterEventResponse> = zulipService.registerEventsQueue()

    fun getEventQueue(queueId: String, lastEventId: Int): Single<GetEventsResponse> =
        zulipService.getEventsQueue(queueId, lastEventId)
}
