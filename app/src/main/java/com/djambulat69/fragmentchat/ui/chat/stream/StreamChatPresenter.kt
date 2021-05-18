package com.djambulat69.fragmentchat.ui.chat.stream

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.ui.chat.BaseChatPresenter
import com.djambulat69.fragmentchat.ui.chat.NO_TOPIC_TITLE
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickTypes
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject
import kotlin.properties.Delegates


class StreamChatPresenter @Inject constructor(
    private val repository: StreamChatRepository
) : BaseChatPresenter<StreamChatView, StreamChatRepository>(repository) {


    override val diffTopics: Boolean = true

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()


    fun initParameters(streamTitle: String, streamId: Int) {
        this.streamTitle = streamTitle
        this.streamId = streamId
    }

    fun sendMessage(messageText: String, _topicTitle: String) {
        val topicTitle = if (_topicTitle.isBlank()) NO_TOPIC_TITLE else _topicTitle

        sendMessageSubscribe(streamId, messageText, topicTitle)
    }

    override fun getMessagesFlowable(): Flowable<List<Message>> =
        repository.getMessages(streamId)

    override fun getNextMessagesSingle(anchor: Long): Single<MessagesResponse> =
        repository.getNextPageMessages(streamTitle, anchor, NEXT_PAGE_SIZE)

    override fun markAsReadCompletable(): Completable =
        repository.markStreamAsRead(streamId)

    override fun updateMessagesSingle(): Single<MessagesResponse> =
        repository.updateMessages(streamTitle, streamId, NEWEST_ANCHOR_MESSAGE, INITIAL_PAGE_SIZE)

    override fun handleClick(click: ChatClickTypes) {
        when (click) {
            is ChatClickTypes.AddEmojiClick,
            is ChatClickTypes.ReactionClick,
            is ChatClickTypes.MessageLongClick -> super.handleClick(click)

            is ChatClickTypes.TopicTitleClick -> {
                viewState.openTopicChat(click.topicName)
            }
        }
    }

}
