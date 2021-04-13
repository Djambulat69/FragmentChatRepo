package com.djambulat69.fragmentchat.ui.channels.streams

import android.util.Log
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.ui.channels.ChannelsPages
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
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
        subscribeOnDb()
        getStreams()
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

    private fun showStreams() {
        viewState.showStreams(recyclerUiItems)
    }

    private fun getStreams() {
        compositeDisposable.add(
            getStreamsFromNetwork()
                .subscribeOn(Schedulers.io())
                .flattenAsObservable { streamResponse -> streamResponse.streams }
                .flatMapSingle { stream ->
                    repository.getTopicsFromNetwork(stream.streamId)
                        .zipWith(Single.just(stream)) { topicsResponse, _ ->
                            stream.apply {
                                topics = topicsResponse.topics
                                isSubscribed = tabPosition == ChannelsPages.SUBSCRIBED.ordinal
                            }
                        }.subscribeOn(Schedulers.io()).retry()
                }
                .toList()
                .flatMapCompletable { streams -> repository.saveStreams(streams) }
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

    private fun subscribeOnDb() {
        compositeDisposable.add(
            getStreamsFromDb()
                .subscribeOn(Schedulers.io())
                .map { streams ->
                    this.streams = streams
                    streamsToStreamUIs(streams)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs -> showDbStreamUIsIfNotEmpty(streamUIs) },
                    { exception ->
                        viewState.showError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }

    private fun streamsToStreamUIs(streams: List<Stream>): List<StreamUI> = streams.map { stream ->
        StreamUI(
            stream,
            expand = { isChecked, topicUIs, position -> toggleStreamItem(isChecked, topicUIs, position) },
            openTopic = { topic -> viewState.openTopicFragment(topic, stream.name, stream.streamId) }
        )
    }

    private fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int) {
        recyclerUiItems = recyclerUiItems.toMutableList().apply {
            if (isChecked) {
                addAll(position + 1, topicUIs)
            } else {
                val topics = topicUIs.map { it.topic }
                removeAll { streamUi -> streamUi is TopicUI && streamUi.topic in topics }
            }
        }
        showStreams()
    }

    private fun getStreamsFromNetwork(): Single<out StreamsResponseSealed> = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> repository.getSubscribedStreamsFromNetwork()
        ChannelsPages.ALL_STREAMS.ordinal -> repository.getAllStreamsFromNetwork()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    }

    private fun getStreamsFromDb(): Flowable<List<Stream>> = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> repository.getSubscribedStreamsFromDb()
        ChannelsPages.ALL_STREAMS.ordinal -> repository.getAllStreamsFromDb()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    }

    private fun showDbStreamUIsIfNotEmpty(streamUIs: List<StreamUI>) {
        if (streamUIs.isEmpty()) {
            viewState.showLoading()
        } else {
            recyclerUiItems = streamUIs
            showStreams()
        }
    }

}
