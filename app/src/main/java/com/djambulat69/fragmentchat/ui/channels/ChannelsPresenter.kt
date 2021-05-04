package com.djambulat69.fragmentchat.ui.channels

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MILLIS = 300L

class ChannelsPresenter @Inject constructor() : MvpPresenter<ChannelsView>() {

    private var lastSearchQuery = ""
    private var viewDisposable = CompositeDisposable()

    fun susbcribeOnSearching(searchObservable: Observable<String>) {
        viewDisposable.add(
            searchObservable
                .subscribeOn(Schedulers.io())
                .debounce(SEARCH_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .filter { query ->
                    query != lastSearchQuery
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { query ->
                    searchStreams(query)
                }
        )
    }

    fun unsubscribeFromViews() {
        viewDisposable.clear()
    }

    private fun searchStreams(query: String) {
        viewState.makeSearch(query)
        lastSearchQuery = query
    }
}
