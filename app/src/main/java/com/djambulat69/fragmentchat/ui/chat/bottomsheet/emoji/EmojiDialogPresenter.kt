package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import javax.inject.Inject

class EmojiDialogPresenter @Inject constructor() : MvpPresenter<EmojiDialogView>() {

    private val viewDisposable = CompositeDisposable()

    fun subscribeOnClicks(clicks: Observable<EmojiClickTypes>) {
        viewDisposable.add(
            clicks
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    handleClick(it)
                }
        )
    }

    fun unsubscribeFromViews() {
        viewDisposable.clear()
    }

    private fun handleClick(it: EmojiClickTypes) {
        when (it) {
            is EmojiClickTypes.EmojiClick -> {
                viewState.setResultAndClose(it.emojiUI.emoji.nameInZulip)
            }
        }
    }
}
