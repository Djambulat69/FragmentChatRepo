package com.djambulat69.fragmentchat.ui.channels

import android.util.Log
import com.djambulat69.fragmentchat.model.network.Subscription
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "ChannelsPresenter"
private const val SEARCH_DEBOUNCE_MILLIS = 300L

class ChannelsPresenter @Inject constructor(
    private val repository: ChannelsRepository
) : MvpPresenter<ChannelsView>() {

    private var lastSearchQuery = ""

    private var compositeDisposable = CompositeDisposable()
    private var viewDisposable = CompositeDisposable()

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

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

    fun createStream(name: String, description: String, inviteOnly: Boolean) {
        compositeDisposable.add(
            repository.subscribeOnStream(Subscription(name, description), inviteOnly)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        viewState.showStreamCreatedSnackbar()
                        viewState.updateStreams()
                    },
                    { e ->
                        viewState.showError()
                        Log.d(TAG, e.stackTraceToString())
                    }
                )
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
