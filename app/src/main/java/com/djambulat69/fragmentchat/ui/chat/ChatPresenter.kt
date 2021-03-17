package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.db.DataBase
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "ChatPresenter"

class ChatPresenter : MvpPresenter<ChatView>() {
    private val compositeDisposable = CompositeDisposable()
    private val messagesSubject = DataBase.messages
    var id: Long = 0

    fun dispose() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.clear()
    }

    fun observeSending(observable: Observable<Message>) {
        val sendingDisposable = observable
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<Message>() {
                override fun onNext(message: Message) {
                    DataBase.sendMessage(message)
                }

                override fun onError(e: Throwable?) {

                }

                override fun onComplete() {

                }

            })
        compositeDisposable.add(sendingDisposable)
    }

    fun updateReactionInMessage(message: Message, reactions: MutableList<Reaction>) =
        DataBase.updateReactionInMessage(message, reactions)

    fun observeMessages() {
        val messagesDisposable = messagesSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<List<Message>>() {
                override fun onNext(messages: List<Message>?) {
                    viewState.showMessages(messages.orEmpty())
                    Log.i(TAG, messages.toString())
                }

                override fun onError(e: Throwable?) {

                }

                override fun onComplete() {

                }

            })
        compositeDisposable.add(messagesDisposable)
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) =
        DataBase.addReactionToMessage(message, emojiCode)
}
