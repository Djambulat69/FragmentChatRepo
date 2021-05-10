package com.djambulat69.fragmentchat.model.network

import com.djambulat69.fragmentchat.ui.channels.streams.StreamsResponseSealed
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun getUserPresence(idOrEmail: String): Single<UserPresenceResponse> = zulipService.getUserPresence(idOrEmail)


    fun sendMessageCompletable(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessage(MESSAGE_TYPE, streamId, text, topicTitle)

    fun addReaction(messageId: Int, emojiName: String): Completable = zulipService.addReaction(messageId, emojiName)


    fun deleteMessage(id: Int): Completable = zulipService.deleteMessage(id)

    fun deleteReaction(messageId: Int, emojiName: String): Completable = zulipService.deleteReaction(messageId, emojiName)


    fun markStreamAsRead(id: Int): Completable = zulipService.markStreamAsRead(id)

    fun markTopicAsRead(streamId: Int, topicTitle: String): Completable = zulipService.markTopicAsRead(streamId, topicTitle)

    fun uploadFile(bytes: ByteArray, type: String, fileName: String): Single<FileResponse> {
        val body = bytes.toRequestBody(type.toMediaType())
        val part = MultipartBody.Part.createFormData("file", fileName, body)

        return zulipService.uploadFile(part)
    }

    fun subscribeOnStream(subscription: Subscription, inviteOnly: Boolean): Completable =
        zulipService.subscribeOnStreams(
            Json.encodeToString(listOf(subscription)),
            inviteOnly
        )

    fun editMessageText(id: Int, newText: String): Completable = zulipService.editMessageText(id, newText)

    fun changeMessageTopic(id: Int, newTopic: String): Completable = zulipService.changeMessageTopic(id, newTopic)

}
