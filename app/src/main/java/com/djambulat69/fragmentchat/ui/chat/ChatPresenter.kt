package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.Topic
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "ChatPresenter"
private const val DB_MESSAGES_LOAD_DEBOUNCE = 100L

class ChatPresenter(
    val topic: Topic,
    private val streamTitle: String,
    val streamId: Int,
    private val repository: ChatRepository
) : MvpPresenter<ChatView>() {

    private val compositeDisposable = CompositeDisposable()

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
        repository.sendMessage(streamId, messageText, topic.name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessages() },
                { exception -> showError(exception) }
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

    private fun getMessages() {
        compositeDisposable.add(
            repository.getMessagesFromNetwork(streamTitle, topic.name)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse -> messagesResponse.messages }
                .flatMapCompletable { messages ->
                    repository.clearTopicMessages(topic.name, streamId).andThen(repository.saveMessages(messages.takeLast(50)))
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
                .subscribe(
                    { messages ->
                        showDbMessagesIfNotEmpty(messages)
                    },
                    { exception -> showError(exception) }
                )
        )
    }

    private fun showDbMessagesIfNotEmpty(messages: List<Message>) {
        if (messages.isEmpty()) {
            viewState.showLoading()
        } else {
            viewState.showMessages(messages)
        }
    }

    private fun showError(exception: Throwable) {
        viewState.showError()
        Log.d(TAG, exception.stackTraceToString())
    }

}
