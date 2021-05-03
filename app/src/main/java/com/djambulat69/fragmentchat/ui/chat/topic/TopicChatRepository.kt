package com.djambulat69.fragmentchat.ui.chat.topic

import com.djambulat69.fragmentchat.model.db.MessagesDao
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class TopicChatRepository @Inject constructor(
    private val messagesDao: MessagesDao,
    private val zulipService: ZulipServiceHelper
) {

    fun getMessages(topicTitle: String, streamId: Int): Flowable<List<Message>> =
        messagesDao.getTopicMessages(topicTitle, streamId)

    fun updateMessages(
        streamTitle: String,
        topicTitle: String,
        streamId: Int,
        anchor: Long,
        count: Int
    ): Single<MessagesResponse> =
        zulipService.getTopicMessagesSingle(streamTitle, topicTitle, anchor, count)
            .flatMap { messagesResponse ->
                clearAndLoadNewMessages(topicTitle, streamId, messagesResponse)
            }

    fun getNextPageMessages(
        streamTitle: String,
        topicTitle: String,
        anchor: Long,
        count: Int
    ): Single<MessagesResponse> =
        zulipService.getTopicMessagesSingle(streamTitle, topicTitle, anchor, count)
            .flatMap { messagesResponse ->
                messagesDao.saveMessages(messagesResponse.messages).andThen(Single.just(messagesResponse))
            }

    fun sendMessage(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessageCompletable(streamId, text, topicTitle)

    fun addReaction(messageId: Int, emojiName: String): Completable =
        zulipService.addReaction(messageId, emojiName)

    fun deleteReaction(messageId: Int, emojiName: String): Completable =
        zulipService.deleteReaction(messageId, emojiName)

    private fun clearAndLoadNewMessages(
        topicTitle: String,
        streamId: Int,
        messagesResponse: MessagesResponse
    ) =
        messagesDao.deleteTopicMessages(topicTitle, streamId)
            .andThen(messagesDao.saveMessages(messagesResponse.messages))
            .andThen(Single.just(messagesResponse))
}
