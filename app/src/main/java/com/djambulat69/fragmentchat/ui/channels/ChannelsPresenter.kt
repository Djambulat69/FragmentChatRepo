package com.djambulat69.fragmentchat.ui.channels

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

class ChannelsPresenter : MvpPresenter<ChannelsView>() {

    private val compositeDisposable = CompositeDisposable()

    private var lastSearchQuery = ""

    fun observeSearchText(searchObservable: Observable<String>) {
        compositeDisposable.add(
            searchObservable
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter { query -> query != lastSearchQuery }
                .subscribe { query ->
                    viewState.makeSearch(query)
                    lastSearchQuery = query
                }
        )
    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }
}
