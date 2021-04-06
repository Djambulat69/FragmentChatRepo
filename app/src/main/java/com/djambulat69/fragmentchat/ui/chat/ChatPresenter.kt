package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.model.Message1
import com.djambulat69.fragmentchat.model.Reaction1
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.model.network.ZulipRemote
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "ChatPresenter"

class ChatPresenter(val topic: Topic, val streamTitle: String) : MvpPresenter<ChatView>() {

    var streamId: Int? = null

    private val compositeDisposable = CompositeDisposable()
    private val zulipRemote = ZulipRemote

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
        getStreamId()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun sendMessage(messageText: String) {
        zulipRemote.sendMessageMaybe(streamId!!, messageText, topic.name)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { getMessages() },
                { exception ->
                    viewState.showError()
                    Log.e(TAG, exception.stackTraceToString())
                }
            )

    }

    fun updateReactionsInMessage(message: Message1, reactions: MutableList<Reaction1>) {}

    fun addReactionToMessage(messageId: String, emojiCode: Int) {}

    private fun getMessages() {
        compositeDisposable.add(
            zulipRemote.getTopicMessagesSingle(streamTitle, topic.name)
                .subscribeOn(Schedulers.io())
                .map { messagesResponse ->
                    messagesResponse.messages
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .subscribe(
                    { messages ->
                        viewState.showMessages(messages)
                    },
                    { exception ->
                        viewState.showError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }

    private fun getStreamId() {
        compositeDisposable.add(
            zulipRemote.getStreamIdSingle(streamTitle)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { streamIdResponse -> this.streamId = streamIdResponse.streamId },
                    { exception ->
                        viewState.showError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }
}
