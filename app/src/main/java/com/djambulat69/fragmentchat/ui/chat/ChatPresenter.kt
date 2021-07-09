package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import android.util.Log
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickTypes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates

class ChatPresenter @Inject constructor(private val repository: ChatRepositoryImpl) : MvpPresenter<ChatView>() {

    var hasMoreMessages = true


    private val compositeDisposable = CompositeDisposable()
    private val viewDisposable = CompositeDisposable()

    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()
    private var topicTitle: String? = null

    private val diffTopics get() = topicTitle == null

    private var isNextPageLoading = false


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        if (NetworkChecker.isConnected()) updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun initParameters(streamTitle: String, streamId: Int, topicTitle: String?) {
        this.streamTitle = streamTitle
        this.streamId = streamId
        this.topicTitle = topicTitle
        viewState.setTopic(topicTitle)
    }

    fun sendMessage(messageText: String, _topicTitle: String) {
        val topicTitle = if (_topicTitle.isBlank()) NO_TOPIC_TITLE else _topicTitle

        sendMessageSubscribe(streamId, messageText, topicTitle)
    }

    fun subscribeOnScrolling(scrollObservable: Observable<Long>) {
        viewDisposable.add(
            scrollObservable
                .subscribeOn(Schedulers.io())
                .debounce(SCROLL_EMIT_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe { anchor ->
                    loadNextMessages(anchor)
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

    fun addReactionInMessage(messageId: Int, emojiName: String) {
        compositeDisposable.add(
            repository.addReaction(messageId, emojiName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    fun removeReactionInMessage(messageId: Int, emojiName: String) {
        compositeDisposable.add(
            repository.deleteReaction(messageId, emojiName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    fun uploadFile(uri: Uri, type: String, name: String) {
        compositeDisposable.add(
            repository.uploadFile(uri, type, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.setFileLoading(true) }
                .doFinally { viewState.setFileLoading(false) }
                .subscribe(
                    { fileResponse -> viewState.attachUriToMessage(fileResponse.uri) },
                    { e -> showError(e) }
                )
        )
    }

    fun editMessageText(id: Int, newText: String) {
        compositeDisposable.add(
            repository.editMessageText(id, newText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { e -> showError(e) }
                )
        )
    }

    fun deleteMessage(id: Int) {
        compositeDisposable.add(
            repository.deleteMessage(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { e -> showError(e) }
                )
        )
    }

    fun changeMessageTopic(id: Int, newTopic: String) {
        compositeDisposable.add(
            repository.changeMessageTopic(id, newTopic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::updateMessages, ::showError)
        )
    }

    fun showEmojiBottomSheet(messageId: Int) = viewState.showEmojiBottomSheet(messageId)

    private fun updateMessages() {
        compositeDisposable.add(
            repository.updateMessages(
                streamTitle, topicTitle, streamId,
                NEWEST_ANCHOR_MESSAGE,
                INITIAL_PAGE_SIZE
            )
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .flatMapCompletable { markAsReadCompletable() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.EMPTY_ACTION,
                    { e -> Log.d(TAG, e.stackTraceToString()) }
                )
        )
    }

    private fun getMessages() {
        compositeDisposable.add(
            getMessagesFlowable()
                .subscribeOn(Schedulers.io())
                .map { messages ->
                    messagesByDate(messages, diffTopics)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.setLoading(true) }
                .doOnNext { viewState.setLoading(false) }
                .filter { it.isNotEmpty() }
                .subscribe(
                    { messages -> viewState.showMessages(messages) },
                    { exception -> showError(exception) }
                )
        )
    }

    private fun loadNextMessages(anchor: Long) {
        if (isNextPageLoading || !hasMoreMessages) return
        isNextPageLoading = true
        compositeDisposable.add(
            repository.getNextMessages(streamTitle, topicTitle, anchor, NEXT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .doFinally { isNextPageLoading = false }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.emptyConsumer(),
                    { exception -> showError(exception) }
                )
        )
    }

    private fun sendMessageSubscribe(streamId: Int, messageText: String, topicName: String?) {
        compositeDisposable.add(
            repository.sendMessage(streamId, messageText, topicName ?: topicTitle.orEmpty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    private fun getMessagesFlowable(): Flowable<List<Message>> =
        topicTitle?.let { repository.getTopicMessages(it, streamId) } ?: repository.getStreamMessages(streamId)


    private fun markAsReadCompletable(): Completable =
        topicTitle?.let { repository.markTopicAsRead(streamId, it) } ?: repository.markStreamAsRead(streamId)

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
                topicTitle = click.topicName
                compositeDisposable.clear()
                getMessages()
                viewState.setTopic(topicTitle)
            }
        }
    }

    private fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

    companion object {

        private const val TAG = "ChatPresenter"

        private const val NEXT_PAGE_SIZE = 30
        private const val INITIAL_PAGE_SIZE = 50
        private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
        private const val SCROLL_EMIT_DEBOUNCE_MILLIS = 200L

    }

}
