package com.djambulat69.fragmentchat.ui.streams

import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "StreamsPresenter"

class StreamsPresenter : MvpPresenter<StreamsView>() {

    private val compositeDisposable = CompositeDisposable()

    var streamUIs: List<ViewTyped> = emptyList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getStreams()
    }

    fun showStreams() {
        viewState.showStreams(streamUIs)
    }

    fun searchStreams(query: String) {
        compositeDisposable.add(
            DataBase.searchStreams(query)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .delay(500, TimeUnit.MILLISECONDS)
                .map { searchedStreams ->
                    streamUIs = streamsToStreamUIs(searchedStreams)
                    streamUIs
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.streamUIs = streamUIs
                        showStreams()
                    },
                    { viewState.showError() }
                )
        )
    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun getStreams() {
        compositeDisposable.add(
            DataBase.streamsSingle
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .delay(2, TimeUnit.SECONDS)
                .map { streams ->
                    streamUIs = streamsToStreamUIs(streams)
                    streamUIs
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.streamUIs = streamUIs
                        showStreams()
                    },
                    { viewState.showError() }
                )
        )
    }

    private fun streamsToStreamUIs(streams: List<Stream>): List<StreamUI> = streams.map {
        StreamUI(it, { isChecked, topicUIs, position ->
            viewState.toggleStreamItem(isChecked, topicUIs, position)
        }) { topic, streamTitle ->
            viewState.openTopicFragment(topic, streamTitle)
        }
    }
}
