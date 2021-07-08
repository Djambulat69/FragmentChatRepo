package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import com.djambulat69.fragmentchat.model.network.FileResponse
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface ChatRepository {

    fun getTopicMessages(topicTitle: String, streamId: Int): Flowable<List<Message>>

    fun getStreamMessages(streamId: Int): Flowable<List<Message>>

    fun updateMessages(
        streamTitle: String, topicTitle: String?,
        streamId: Int, anchor: Long, count: Int
    ): Single<MessagesResponse>

    fun getNextMessages(
        streamTitle: String, topicTitle: String?,
        anchor: Long, count: Int
    ): Single<MessagesResponse>

    fun markTopicAsRead(streamId: Int, topicTitle: String): Completable

    fun markStreamAsRead(streamId: Int): Completable

    fun sendMessage(streamId: Int, messageText: String, topicName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(id: Int): Completable

    fun changeMessageTopic(id: Int, newTopic: String): Completable

    fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse>

    fun editMessageText(id: Int, newText: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

}
