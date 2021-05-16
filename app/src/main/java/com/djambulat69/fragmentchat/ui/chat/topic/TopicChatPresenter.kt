package com.djambulat69.fragmentchat.ui.chat.topic

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.ui.chat.BaseChatPresenter
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import kotlin.properties.Delegates


private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
private const val INITIAL_PAGE_SIZE = 50

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

    fun subscribeOnSendingMessages(sendObservable: Observable<String>) {
        viewDisposable.add(
            sendObservable
                .subscribeOn(Schedulers.io())
                .subscribe { messageText -> sendMessage(messageText) }
        )
    }

    override fun getMessagesFlowable(): Flowable<List<Message>> =
        repository.getMessages(topicTitle, streamId)

    override fun getNextMessagesSingle(anchor: Long, count: Int): Single<MessagesResponse> =
        repository.getNextPageMessages(streamTitle, topicTitle, anchor, count)

    override fun markAsReadCompletable(): Completable =
        repository.markTopicAsRead(streamId, topicTitle)

    override fun updateMessagesSingle(): Single<MessagesResponse> =
        repository.updateMessages(streamTitle, topicTitle, streamId, NEWEST_ANCHOR_MESSAGE, INITIAL_PAGE_SIZE)

    private fun sendMessage(messageText: String) {
        sendMessageSubscribe(streamId, messageText, topicTitle)
    }

}
