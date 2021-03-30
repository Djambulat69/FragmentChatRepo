package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.model.db.DataBase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "ChatPresenter"

class ChatPresenter : MvpPresenter<ChatView>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getMessages()
    }

    fun observeSending(observable: Observable<Message>) {
        compositeDisposable.add(
            observable
                .observeOn(Schedulers.io())
                .subscribe { message ->
                    compositeDisposable.add(
                        DataBase.sendMessage(message)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { },
                                { viewState.showError() }
                            )
                    )
                }
        )
    }

    fun updateReactionsInMessage(message: Message, reactions: MutableList<Reaction>) =
        DataBase.updateReactionsInMessage(message, reactions)

    fun observeMessages() {
        compositeDisposable.add(DataBase.messagesSubject
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages -> viewState.showMessages(messages.orEmpty()) },
                { viewState.showError() }
            ))
    }

    private fun getMessages() {
        compositeDisposable.add(DataBase.messagesSingle
            .subscribeOn(Schedulers.io())
            .doOnSubscribe { viewState.showLoading() }
            .delay(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { messages -> viewState.showMessages(messages.orEmpty()) },
                { viewState.showError() }
            ))
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) = DataBase.addReactionToMessage(message, emojiCode)

    fun dispose() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.clear()
    }
}
