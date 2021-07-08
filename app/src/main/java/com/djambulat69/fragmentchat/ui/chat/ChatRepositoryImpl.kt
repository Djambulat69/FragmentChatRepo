package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import com.djambulat69.fragmentchat.model.UriReader
import com.djambulat69.fragmentchat.model.db.MessagesDao
import com.djambulat69.fragmentchat.model.network.FileResponse
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class ChatRepositoryImpl @Inject constructor(
    private val messagesDao: MessagesDao,
    private val zulipService: ZulipServiceHelper,
    private val uriReader: UriReader
) : ChatRepository {

    override fun getTopicMessages(topicTitle: String, streamId: Int): Flowable<List<Message>> =
        messagesDao.getTopicMessages(topicTitle, streamId)

    override fun getStreamMessages(streamId: Int): Flowable<List<Message>> {
        return messagesDao.getStreamMessages(streamId)
    }

    override fun updateMessages(
        streamTitle: String, topicTitle: String?,
        streamId: Int, anchor: Long, count: Int
    ): Single<MessagesResponse> {

        return zulipService.getMessagesSingle(streamTitle, topicTitle, anchor, count)
            .flatMap { messagesResponse ->
                clearAndLoadNewMessages(topicTitle, streamId, messagesResponse)
            }

    }

    override fun getNextMessages(
        streamTitle: String, topicTitle: String?,
        anchor: Long, count: Int
    ): Single<MessagesResponse> {
        return zulipService.getMessagesSingle(streamTitle, topicTitle, anchor, count)
            .flatMap { messagesResponse ->
                messagesDao.saveMessages(messagesResponse.messages).andThen(Single.just(messagesResponse))
            }
    }

    override fun sendMessage(streamId: Int, messageText: String, topicName: String): Completable =
        zulipService.sendMessageCompletable(streamId, messageText, topicName)


    override fun addReaction(messageId: Int, emojiName: String): Completable =
        zulipService.addReaction(messageId, emojiName)

    override fun markTopicAsRead(streamId: Int, topicTitle: String): Completable =
        zulipService.markTopicAsRead(streamId, topicTitle)

    override fun markStreamAsRead(streamId: Int): Completable =
        zulipService.markStreamAsRead(streamId)

    override fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse> {
        return readUri(uri)
            .flatMap { bytes ->
                zulipService.uploadFile(bytes, type, name)
            }
    }

    override fun editMessageText(id: Int, newText: String): Completable = zulipService.editMessageText(id, newText)

    override fun changeMessageTopic(id: Int, newTopic: String): Completable = zulipService.changeMessageTopic(id, newTopic)

    override fun deleteMessage(id: Int): Completable = zulipService.deleteMessage(id)

    override fun deleteReaction(messageId: Int, emojiName: String): Completable =
        zulipService.deleteReaction(messageId, emojiName)

    private fun clearAndLoadNewMessages(
        topicTitle: String?,
        streamId: Int,
        messagesResponse: MessagesResponse
    ) =
        deleteMessagesCompletable(topicTitle, streamId)
            .andThen(messagesDao.saveMessages(messagesResponse.messages))
            .andThen(Single.just(messagesResponse))

    private fun deleteMessagesCompletable(topicTitle: String?, streamId: Int): Completable {
        return topicTitle?.let {
            messagesDao.deleteTopicMessages(it, streamId)
        } ?: messagesDao.deleteStreamMessages(streamId)
    }

    private fun readUri(uri: Uri): Single<ByteArray> {
        return Single.just(uriReader.readBytes(uri))
    }
}
