package com.djambulat69.fragmentchat.ui.channels.streams

import android.util.Log
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.ui.channels.ChannelsPages
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "StreamsPresenter"

class StreamsPresenter(private val tabPosition: Int, private val repository: StreamsRepository) : MvpPresenter<StreamsView>() {

    private val compositeDisposable = CompositeDisposable()

    private var recyclerUiItems: List<ViewTyped> = emptyList()
    private var streams: List<Stream> = emptyList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getStreams()
        updateStreams()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun searchStreams(query: String) {
        compositeDisposable.add(
            Single.fromCallable { streams.filter { it.name.startsWith(query, ignoreCase = true) } }
                .subscribeOn(Schedulers.computation())
                .map { searchedStreams -> streamsToStreamUIs(searchedStreams) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.recyclerUiItems = streamUIs
                        showStreams()
                    },
                    { viewState.showError() }
                )
        )
    }

    fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int) {
        recyclerUiItems = recyclerUiItems.toMutableList().apply {
            if (isChecked) {
                addAll(position + 1, topicUIs)
            } else {
                val topics = topicUIs.map { it.topic }
                removeAll { itemUi -> itemUi is TopicUI && itemUi.topic in topics }
            }
        }
        showStreams()
    }

    private fun showStreams() {
        viewState.showStreams(recyclerUiItems)
    }

    private fun updateStreams() {
        compositeDisposable.add(
            updateStreamsbyTabPosition()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    Functions.EMPTY_ACTION,
                    { exception ->
                        viewState.showToastError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }

    private fun getStreams() {
        compositeDisposable.add(
            getStreamsbyTabPosition()
                .subscribeOn(Schedulers.io())
                .map { streams ->
                    this.streams = streams
                    streamsToStreamUIs(streams)
                }
                .filter { it.isNotEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewState.showLoading() }
                .subscribe(
                    { streamUIs ->
                        recyclerUiItems = streamUIs
                        showStreams()
                    },
                    { exception ->
                        viewState.showError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }

    private fun streamsToStreamUIs(streams: List<Stream>): List<StreamUI> =
        streams.map { stream -> StreamUI(stream) }

    private fun updateStreamsbyTabPosition(): Completable = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> repository.updateSubscribedStreams()
        ChannelsPages.ALL_STREAMS.ordinal -> repository.updateAllStreams()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    }

    private fun getStreamsbyTabPosition(): Flowable<List<Stream>> = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> repository.getSubscribedStreams()
        ChannelsPages.ALL_STREAMS.ordinal -> repository.getAllStreams()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    }

}
