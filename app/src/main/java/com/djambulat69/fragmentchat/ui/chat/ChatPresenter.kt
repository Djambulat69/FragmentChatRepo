package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
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

    private val compositeDisposable = CompositeDisposable()

    var isNextPageLoading = false
    var hasMoreMessages = true
    var shouldScrollBottom = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        subscribeOnDbMessages()
        getMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun sendMessage(messageText: String) {
        shouldScrollBottom = true
        compositeDisposable.add(
            repository.sendMessage(streamId, messageText, topic.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { getMessages() },
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
                    { getMessages() },
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
                    { getMessages() },
                    { exception -> showError(exception) }
                )
        )
    }

    fun getNextMessages(anchor: Long) {
        compositeDisposable.add(
            repository.getMessagesFromNetwork(streamTitle, topic.name, anchor, count = INITIAL_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .flatMapCompletable { messages ->
                    repository.saveMessages(messages)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { isNextPageLoading = false },
                    { exception ->
                        isNextPageLoading = false
                        showError(exception)
                    }
                )
        )
    }

    private fun getMessages() {
        compositeDisposable.add(
            repository.getMessagesFromNetwork(streamTitle, topic.name, NEWEST_ANCHOR_MESSAGE, count = NEXT_PAGE_SIZE)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    hasMoreMessages = !messagesResponse.foundOldest
                    messagesResponse.messages
                }
                .flatMapCompletable { messages ->
                    repository.clearTopicMessages(topic.name, streamId).andThen(repository.saveMessages(messages))
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.EMPTY_ACTION,
                    { exception -> showError(exception) }
                )
        )
    }

    private fun subscribeOnDbMessages() {
        compositeDisposable.add(
            repository.getMessagesFromDb(topic.name, streamId)
                .subscribeOn(Schedulers.io())
                .debounce(DB_MESSAGES_LOAD_DEBOUNCE, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .filter { it.isNotEmpty() }
                .subscribe(
                    { messages ->
                        viewState.showMessages(messages)
                    },
                    { exception -> showError(exception) }
                )
        )
    }

    private fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

}
