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
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
//        observeMessages()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun sendMessage(message: Message1) {}

    fun updateReactionsInMessage(message: Message1, reactions: MutableList<Reaction1>) {}

    fun addReactionToMessage(messageId: String, emojiCode: Int) {}

//    private fun observeMessages() {
//        compositeDisposable.add(
//            DataBase.messagesSubject
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { messages -> viewState.showMessages(messages.orEmpty()) },
//                    { viewState.showError() }
//                ))
//    }

    private fun getMessages() {
        compositeDisposable.add(
            ZulipRemote.getTopicMessagesSingle(streamTitle, topic.name)
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
}
