package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.channels.ChannelsPages
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "StreamsPresenter"

class StreamsPresenter(private val tabPosition: Int) : MvpPresenter<StreamsView>() {

    private val compositeDisposable = CompositeDisposable()

    private var streamUIs: List<ViewTyped> = emptyList()
        set(value) {
            field = when (tabPosition) {
                ChannelsPages.SUBSCRIBED.ordinal -> value.filter {
                    if (it is StreamUI)
                        it.stream.isSubscribed
                    else
                        true
                }

                else -> value
            }
        }

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
    }

    private fun showStreams() {
        viewState.showStreams(streamUIs)
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
        StreamUI(it, { isChecked, topicUIs, position -> toggleStreamItem(isChecked, topicUIs, position) })
        { topic, streamTitle -> viewState.openTopicFragment(topic, streamTitle) }
    }

    private fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int) {
        streamUIs = streamUIs.toMutableList().apply {
            if (isChecked) {
                addAll(position + 1, topicUIs)
            } else {
                removeAll(topicUIs)
            }
        }
        showStreams()
    }
}
