package com.djambulat69.fragmentchat.ui.chat.topic

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.ui.chat.BaseChatPresenter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import kotlin.properties.Delegates


class TopicChatPresenter @Inject constructor(
    private val repository: TopicChatRepository
) : BaseChatPresenter<TopicChatView, TopicChatRepository>(repository) {

    override val diffTopics: Boolean = false

    private lateinit var topicTitle: String
    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()


    fun initParameters(topicTitle: String, streamTitle: String, streamId: Int) {
        this.topicTitle = topicTitle
        this.streamTitle = streamTitle
        this.streamId = streamId
    }

    fun sendMessage(messageText: String) {
        sendMessageSubscribe(streamId, messageText, topicTitle)
    }

    override fun getMessagesFlowable(): Flowable<List<Message>> =
        repository.getMessages(topicTitle, streamId)

    override fun getNextMessagesSingle(anchor: Long): Single<MessagesResponse> =
        repository.getNextPageMessages(streamTitle, topicTitle, anchor, NEXT_PAGE_SIZE)

    override fun markAsReadCompletable(): Completable =
        repository.markTopicAsRead(streamId, topicTitle)

    override fun updateMessagesSingle(): Single<MessagesResponse> =
        repository.updateMessages(streamTitle, topicTitle, streamId, NEWEST_ANCHOR_MESSAGE, INITIAL_PAGE_SIZE)


}
