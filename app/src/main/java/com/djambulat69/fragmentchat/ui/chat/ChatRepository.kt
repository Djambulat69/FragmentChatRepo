package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.model.db.MessagesDao
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single


class ChatRepository(private val messagesDao: MessagesDao) {

    private val zulipService = ZulipServiceImpl

    fun getMessagesFromDb(topicTitle: String, streamId: Int): Flowable<List<Message>> =
        messagesDao.getMessages(topicTitle, streamId)

    fun saveMessages(messages: List<Message>): Completable =
        messagesDao.saveMessages(messages)

    fun clearTopicMessages(topicTitle: String, streamId: Int): Completable =
        messagesDao.deleteTopicMessages(topicTitle, streamId)

    fun getMessagesFromNetwork(
        streamTitle: String,
        topicTitle: String,
        anchor: Long,
        count: Int
    ): Single<MessagesResponse> =
        zulipService.getTopicMessagesSingle(streamTitle, topicTitle, anchor, count)

    fun sendMessage(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessageCompletable(streamId, text, topicTitle)

    fun addReaction(messageId: Int, emojiName: String): Completable =
        zulipService.addReaction(messageId, emojiName)

    fun deleteReaction(messageId: Int, emojiName: String): Completable =
        zulipService.deleteReaction(messageId, emojiName)

}
