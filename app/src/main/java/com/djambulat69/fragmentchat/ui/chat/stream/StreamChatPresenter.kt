package com.djambulat69.fragmentchat.ui.chat.stream

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.ui.chat.BaseChatPresenter
import com.djambulat69.fragmentchat.ui.chat.NO_TOPIC_TITLE
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickTypes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates


private const val SCROLL_EMIT_THROTTLE_MILLIS = 100L

class StreamChatPresenter @Inject constructor(
    private val repository: StreamChatRepository
) : BaseChatPresenter<StreamChatView, StreamChatRepository>(repository) {


    override val diffTopics: Boolean = true

    private val viewDisposable = CompositeDisposable()

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()


    override fun getMessagesFlowable(): Flowable<List<Message>> =
        repository.getMessages(streamId)

    override fun getNextMessagesSingle(anchor: Long, count: Int): Single<MessagesResponse> =
        repository.getNextPageMessages(streamTitle, anchor, count)

    override fun markAsReadCompletable(): Completable =
        repository.markStreamAsRead(streamId)

    override fun updateMessagesSingle(newestMessageAnchor: Long, initialPageSize: Int): Single<MessagesResponse> =
        repository.updateMessages(streamTitle, streamId, newestMessageAnchor, initialPageSize)


    fun initParameters(streamTitle: String, streamId: Int) {
        this.streamTitle = streamTitle
        this.streamId = streamId
    }

    fun subscribeOnSendingMessages(sendObservable: Observable<Pair<String, String>>) {
        viewDisposable.add(
            sendObservable
                .subscribeOn(Schedulers.io())
                .subscribe { textAndTopic -> sendMessage(textAndTopic.first, textAndTopic.second) }
        )
    }

    fun subscribeOnScrolling(scrollObservable: Observable<Long>) {
        viewDisposable.add(
            scrollObservable
                .subscribeOn(Schedulers.io())
                .debounce(SCROLL_EMIT_THROTTLE_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe { anchor ->
                    getNextMessages(anchor)
                }
        )
    }

    fun subscribeOnClicks(clicks: Observable<ChatClickTypes>) {
        viewDisposable.add(
            clicks
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleClick(it)
                }
        )
    }

    fun unsubscribeFromViews() = viewDisposable.clear()

    private fun sendMessage(messageText: String, _topicTitle: String) {
        val topicTitle = if (_topicTitle.isBlank()) NO_TOPIC_TITLE else _topicTitle

        sendMessageSubscribe(streamId, messageText, topicTitle)
    }

    private fun handleClick(click: ChatClickTypes) {
        when (click) {
            is ChatClickTypes.AddEmojiClick -> {
                viewState.showEmojiBottomSheet(click.item.message.id)
            }
            is ChatClickTypes.ReactionClick -> {

                if (click.isSelected) {
                    addReactionInMessage(click.messageId, click.emojiName)
                } else {
                    removeReactionInMessage(click.messageId, click.emojiName)
                }
            }
            is ChatClickTypes.MessageLongClick -> {
                viewState.showMessageOptions(click.message)
            }
            is ChatClickTypes.TopicTitleClick -> {
                viewState.openTopicChat(click.topicName)
            }
        }
    }

}
