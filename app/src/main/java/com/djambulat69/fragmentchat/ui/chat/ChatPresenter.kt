package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.model.db.DataBase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "ChatPresenter"

class ChatPresenter : MvpPresenter<ChatView>() {
    private val compositeDisposable = CompositeDisposable()

    fun observeSending(observable: Observable<Message>) {
        compositeDisposable.add(observable
            .observeOn(Schedulers.io())
            .subscribe(
                { message -> DataBase.sendMessage(message) },
                { exception -> Log.e(TAG, exception.stackTraceToString()) }
            )
        )
    }

    fun updateReactionsInMessage(message: Message, reactions: MutableList<Reaction>) =
        DataBase.updateReactionsInMessage(message, reactions)

    fun observeMessages() {
        compositeDisposable.add(DataBase.messagesSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages -> viewState.showMessages(messages.orEmpty()) },
                { exception -> Log.e(TAG, exception.stackTraceToString()) }
            ))
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) = DataBase.addReactionToMessage(message, emojiCode)

    fun dispose() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.clear()
    }
}
