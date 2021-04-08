package com.djambulat69.fragmentchat.ui.channels.streams

import android.util.Log
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.model.network.ZulipRemote
import com.djambulat69.fragmentchat.ui.channels.ChannelsPages
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "StreamsPresenter"


class StreamsPresenter(tabPosition: Int) : MvpPresenter<StreamsView>() {

    private val zulipService = ZulipRemote
    private val compositeDisposable = CompositeDisposable()

    private val streamsSingle: Single<StreamsResponseSealed> = getStreamsSingle(tabPosition)
    private var recyclerItemUIs: List<ViewTyped> = emptyList()
    private var streams: List<Stream> = emptyList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getStreams()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun searchStreams(query: String) {
        compositeDisposable.add(
            Single.fromCallable { streams.filter { it.name.startsWith(query, ignoreCase = true) } }
                .subscribeOn(Schedulers.io())
                .map { searchedStreams -> streamsToStreamUIs(searchedStreams) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.recyclerItemUIs = streamUIs
                        showStreams()
                    },
                    {
                        viewState.showError()
                    }
                )
        )
    }

    private fun showStreams() {
        viewState.showStreams(recyclerItemUIs)
    }

    private fun getStreams() {
        compositeDisposable.add(
            streamsSingle
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .flatMapObservable { streamsResponse: StreamsResponseSealed ->
                    Observable.fromIterable(streamsResponse.streams)
                }
                .flatMapSingle { stream ->
                    zulipService.getTopicsSingle(stream.streamId).zipWith(Single.just(stream)) { topicsResponse, _ ->
                        stream.apply { topics = topicsResponse.topics }
                    }
                }
                .toList()
                .map { streams ->
                    this.streams = streams
                    streamsToStreamUIs(streams)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.recyclerItemUIs = streamUIs
                        showStreams()
                    },
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
            openTopic = { topic, streamTitle -> viewState.openTopicFragment(topic, streamTitle) }
        )
    }

    private fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int) {
        recyclerItemUIs = recyclerItemUIs.toMutableList().apply {
            if (isChecked) {
                addAll(position + 1, topicUIs)
            } else {
                val topics = topicUIs.map { topicUI -> topicUI.topic }
                removeAll { streamUi -> streamUi is TopicUI && streamUi.topic in topics }
            }
        }
        showStreams()
    }

    private fun getStreamsSingle(tabPosition: Int) = when (tabPosition) {
        ChannelsPages.SUBSCRIBED.ordinal -> zulipService.getSubscribtionsSingle()
        ChannelsPages.ALL_STREAMS.ordinal -> zulipService.getStreamsSingle()
        else -> throw IllegalStateException("Undefined StreamsFragment tabPosition: $tabPosition")
    } as Single<StreamsResponseSealed>
}
