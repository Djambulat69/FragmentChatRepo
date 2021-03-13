package com.djambulat69.fragmentchat.ui.chat

import android.util.Log
import com.djambulat69.fragmentchat.db.DataBase
import com.djambulat69.fragmentchat.model.Message
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "ChatPresenter"

class ChatPresenter : MvpPresenter<ChatView>() {
    private val compositeDisposable = CompositeDisposable()
    private val messages = DataBase.messages

    override fun detachView(view: ChatView?) {
        compositeDisposable.clear()
        super.detachView(view)
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

    fun observeMessages() {
        val messagesDisposable = messages
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribeWith(object : DisposableObserver<List<Message>>() {
                override fun onNext(messages: List<Message>?) {
                    viewState.showMessages(messages ?: emptyList())
                    Log.i(TAG, messages.toString())
                }

                override fun onError(e: Throwable?) {

                }

                override fun onComplete() {

                }

            })
        compositeDisposable.add(messagesDisposable)
    }
}
