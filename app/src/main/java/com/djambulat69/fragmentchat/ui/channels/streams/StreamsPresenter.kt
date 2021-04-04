package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.model.network.ZulipRemote
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


class StreamsPresenter(private val tabPosition: Int) : MvpPresenter<StreamsView>() {

    private val compositeDisposable = CompositeDisposable()

    private var recyclerItemUIs: List<ViewTyped> = emptyList()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getStreams()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

/*    fun searchStreams(query: String) {
        compositeDisposable.add(
            DataBase.searchStreams(query)
                .subscribeOn(Schedulers.io())
                .delay(500, TimeUnit.MILLISECONDS)
                .map { searchedStreams ->
                    streamUIs = streamsToStreamUIs(searchedStreams)
                    streamUIs
                }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewState.showLoading()
                }
                .subscribe(
                    { streamUIs ->
                        this.streamUIs = streamUIs
                        showStreams()
                    },
                    {
                        viewState.showError()
                    }
                )
        )
    }*/

    private fun showStreams() {
        viewState.showStreams(recyclerItemUIs)
    }

    private fun getStreams() {
        compositeDisposable.add(
            ZulipRemote.getStreamsSingle()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .flatMapObservable { allStreamsResponse -> Observable.fromIterable(allStreamsResponse.streams) }
                .flatMapSingle { stream ->
                    ZulipRemote.getTopicsSingle(stream.streamId).zipWith(Single.just(stream)) { topicsResponse, _ ->
                        stream.apply { topics = topicsResponse.topics }
                    }
                }
                .toList()
                .map { streams -> streamsToStreamUIs(streams) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { streamUIs ->
                        this.recyclerItemUIs = streamUIs
                        showStreams()
                    },
                    { viewState.showError() }
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
}
