package com.djambulat69.fragmentchat.ui.chat.stream

import com.djambulat69.fragmentchat.model.db.MessagesDao
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class StreamChatRepository @Inject constructor(
    private val messagesDao: MessagesDao,
    private val zulipService: ZulipServiceHelper
) {

    fun getMessages(streamId: Int): Flowable<List<Message>> =
        messagesDao.getStreamMessages(streamId)

    fun updateMessages(
        streamTitle: String,
        streamId: Int,
        anchor: Long,
        count: Int
    ): Single<MessagesResponse> =
        zulipService.getStreamMessagesSingle(streamTitle, anchor, count)
            .flatMap { messagesResponse ->
                clearAndLoadNewMessages(streamId, messagesResponse)
            }

    fun getNextPageMessages(
        streamTitle: String,
        anchor: Long,
        count: Int
    ): Single<MessagesResponse> =
        zulipService.getStreamMessagesSingle(streamTitle, anchor, count)
            .flatMap { messagesResponse ->
                messagesDao.saveMessages(messagesResponse.messages).andThen(Single.just(messagesResponse))
            }

    fun sendMessage(streamId: Int, text: String, topicTitle: String): Completable =
        zulipService.sendMessageCompletable(streamId, text, topicTitle)

    fun addReaction(messageId: Int, emojiName: String): Completable =
        zulipService.addReaction(messageId, emojiName)

    fun editMessageText(id: Int, newText: String): Completable = zulipService.editMessageText(id, newText)

    fun deleteReaction(messageId: Int, emojiName: String): Completable =
        zulipService.deleteReaction(messageId, emojiName)

    private fun clearAndLoadNewMessages(
        streamId: Int,
        messagesResponse: MessagesResponse
    ) =
        messagesDao.deleteStreamMessages(streamId)
            .andThen(messagesDao.saveMessages(messagesResponse.messages))
            .andThen(Single.just(messagesResponse))

}
