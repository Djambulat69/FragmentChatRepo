package com.djambulat69.fragmentchat.ui.chat.topic

import android.net.Uri
import android.util.Log
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickTypes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.properties.Delegates


private const val TAG = "TopicChatPresenter"
private const val DB_MESSAGES_LOAD_DEBOUNCE = 100L
private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
private const val INITIAL_PAGE_SIZE = 50
private const val NEXT_PAGE_SIZE = 30
private const val SCROLL_EMIT_THROTTLE_MILLIS = 100L


class TopicChatPresenter @Inject constructor(
    private val repository: TopicChatRepository
) : MvpPresenter<TopicChatView>() {

    var hasMoreMessages = true

    private var isNextPageLoading = false
    private val compositeDisposable = CompositeDisposable()
    private val viewDisposable = CompositeDisposable()

    private lateinit var topicTitle: String
    private lateinit var streamTitle: String
    private var streamId by Delegates.notNull<Int>()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        if (NetworkChecker.isConnected()) updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

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

    fun uploadFile(uri: Uri, type: String, name: String) {
        compositeDisposable.add(
            repository.uploadFile(uri, type, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .subscribe(
                    { updateMessages() },
                    { e -> showError(e) }
                )
        )
    }

    fun unsubscribeFromViews() = viewDisposable.clear()

    private fun sendMessage(messageText: String) {
        compositeDisposable.add(
            repository.sendMessage(streamId, messageText, topicTitle)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    private fun removeReactionInMessage(messageId: Int, emojiName: String) {
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

    private fun updateMessages() {
        compositeDisposable.add(
            repository.updateMessages(streamTitle, topicTitle, streamId, NEWEST_ANCHOR_MESSAGE, count = INITIAL_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .flatMapCompletable { repository.markTopicAsRead(streamId, topicTitle) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.EMPTY_ACTION,
                    { exception -> showError(exception) }
                )
        )
    }

    private fun getNextMessages(anchor: Long) {
        if (isNextPageLoading || !hasMoreMessages) return
        isNextPageLoading = true
        compositeDisposable.add(
            repository.getNextPageMessages(streamTitle, topicTitle, anchor, NEXT_PAGE_SIZE)
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

    private fun getMessages() {
        compositeDisposable.add(
            repository.getMessages(topicTitle, streamId)
                .subscribeOn(Schedulers.io())
                .debounce(DB_MESSAGES_LOAD_DEBOUNCE, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .filter { it.isNotEmpty() }
                .subscribe(
                    { messages -> viewState.showMessages(messages) },
                    { exception -> showError(exception) }
                )
        )
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
        }
    }

    private fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

}
