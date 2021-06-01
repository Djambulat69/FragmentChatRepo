package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import android.util.Log
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickTypes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit


private const val SCROLL_EMIT_DEBOUNCE_MILLIS = 100L

abstract class BaseChatPresenter<V : BaseChatView, R : ChatRepository>(
    private val repository: R
) : MvpPresenter<V>() {

    abstract val diffTopics: Boolean

    var hasMoreMessages = true

    protected val compositeDisposable = CompositeDisposable()
    protected val viewDisposable = CompositeDisposable()


    private var isNextPageLoading = false

    protected abstract fun getMessagesFlowable(): Flowable<List<Message>>
    protected abstract fun getNextMessagesSingle(anchor: Long): Single<MessagesResponse>
    protected abstract fun updateMessagesSingle(): Single<MessagesResponse>
    protected abstract fun markAsReadCompletable(): Completable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        if (NetworkChecker.isConnected()) updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun subscribeOnScrolling(scrollObservable: Observable<Long>) {
        viewDisposable.add(
            scrollObservable
                .subscribeOn(Schedulers.io())
                .debounce(SCROLL_EMIT_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
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
                .doOnSubscribe { viewState.setMessageLoading(true) }
                .doFinally { viewState.setMessageLoading(false) }
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
                .subscribe(
                    { updateMessages() },
                    { e -> showError(e) }
                )
        )
    }

    fun showEmojiBottomSheet(messageId: Int) = viewState.showEmojiBottomSheet(messageId)

    protected fun sendMessageSubscribe(streamId: Int, messageText: String, topicName: String) {
        compositeDisposable.add(
            repository.sendMessage(streamId, messageText, topicName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    protected fun removeReactionInMessage(messageId: Int, emojiName: String) {
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

    protected fun updateMessages() {
        compositeDisposable.add(
            updateMessagesSingle()
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

    protected fun getNextMessages(anchor: Long) {
        if (isNextPageLoading || !hasMoreMessages) return
        isNextPageLoading = true
        compositeDisposable.add(
            getNextMessagesSingle(anchor)
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

    protected fun getMessages() {
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

    protected fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

    protected open fun handleClick(click: ChatClickTypes) {
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


    companion object {

        protected val TAG = this::class.simpleName

        const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
        const val INITIAL_PAGE_SIZE = 50
        const val NEXT_PAGE_SIZE = 30

    }
}
