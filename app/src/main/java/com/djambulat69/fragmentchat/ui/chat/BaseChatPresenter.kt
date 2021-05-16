package com.djambulat69.fragmentchat.ui.chat

import android.net.Uri
import android.util.Log
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.MessagesResponse
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit


private const val DB_MESSAGES_LOAD_DEBOUNCE = 100L
private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
private const val INITIAL_PAGE_SIZE = 50
private const val NEXT_PAGE_SIZE = 30
private const val SCROLL_EMIT_DEBOUNCE_MILLIS = 100L

abstract class BaseChatPresenter<V : BaseChatView, R : ChatRepository>(
    private val repository: R
) : MvpPresenter<V>() {

    abstract val diffTopics: Boolean

    var hasMoreMessages = true

    protected val compositeDisposable = CompositeDisposable()

    private var isNextPageLoading = false

    abstract fun getMessagesFlowable(): Flowable<List<Message>>
    abstract fun getNextMessagesSingle(anchor: Long, count: Int): Single<MessagesResponse>
    abstract fun updateMessagesSingle(newestMessageAnchor: Long, initialPageSize: Int): Single<MessagesResponse>
    abstract fun markAsReadCompletable(): Completable

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        if (NetworkChecker.isConnected()) updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
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
            updateMessagesSingle(
                NEWEST_ANCHOR_MESSAGE, INITIAL_PAGE_SIZE
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
                    { exception -> showError(exception) }
                )
        )
    }

    protected fun getNextMessages(anchor: Long) {
        if (isNextPageLoading || !hasMoreMessages) return
        isNextPageLoading = true
        compositeDisposable.add(
            getNextMessagesSingle(anchor, NEXT_PAGE_SIZE)
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
                .debounce(DB_MESSAGES_LOAD_DEBOUNCE, TimeUnit.MILLISECONDS)
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


    companion object {

        private val TAG = this::class.simpleName

    }
}
