package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import com.djambulat69.fragmentchat.model.network.Topic
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "ChatPresenter"
private const val DB_MESSAGES_LOAD_DEBOUNCE = 100L
private const val NEWEST_ANCHOR_MESSAGE = 10000000000000000
private const val INITIAL_PAGE_SIZE = 50
private const val NEXT_PAGE_SIZE = 30

class ChatPresenter(
    val topic: Topic,
    private val streamTitle: String,
    val streamId: Int,
    private val repository: ChatRepository
) : MvpPresenter<ChatView>() {

    var hasMoreMessages = true

    private var isOnline = NetworkChecker.isConnected()
    private val compositeDisposable = CompositeDisposable()
    private var isNextPageLoading = false


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        if (isOnline) updateMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun sendMessage(messageText: String) {
        compositeDisposable.add(
            repository.sendMessage(streamId, messageText, topic.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateMessages() },
                    { exception -> showError(exception) }
                )
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

    fun getNextMessages(anchor: Long) {
        if (isNextPageLoading || !hasMoreMessages) return
        isNextPageLoading = true
        compositeDisposable.add(
            repository.getNextPageMessages(streamTitle, topic.name, anchor, NEXT_PAGE_SIZE)
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

    fun updateMessages() {
        isOnline = true
        compositeDisposable.add(
            repository.updateMessages(streamTitle, topic.name, streamId, NEWEST_ANCHOR_MESSAGE, count = INITIAL_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.emptyConsumer(),
                    { exception -> showError(exception) }
                )
        )
    }

    private fun getMessages() {
        compositeDisposable.add(
            repository.getMessages(topic.name, streamId)
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

    private fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

}
