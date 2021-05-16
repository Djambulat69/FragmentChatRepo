package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import com.djambulat69.fragmentchat.model.network.FileResponse
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface ChatRepository {

    fun sendMessage(streamId: Int, messageText: String, topicName: String): Completable

    fun deleteReaction(messageId: Int, emojiName: String): Completable

    fun deleteMessage(id: Int): Completable

    fun changeMessageTopic(id: Int, newTopic: String): Completable

    fun uploadFile(uri: Uri, type: String, name: String): Single<FileResponse>

    fun editMessageText(id: Int, newText: String): Completable

    fun addReaction(messageId: Int, emojiName: String): Completable

}
