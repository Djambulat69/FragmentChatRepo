package com.djambulat69.fragmentchat.ui.streams

import android.util.Log
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
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

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    fun toggleStreamItem(isChecked: Boolean, topicUis: List<TopicUI>, position: Int) {
        viewState.toggleStreamItem(isChecked, topicUis, position)
    }

    fun openTopicFragment(topic: Topic, streamTitle: String) {
        viewState.openTopicFragment(topic, streamTitle)
    }

    private fun getStreams() {
        compositeDisposable.add(
            DataBase.streamsSingle
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
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
                    { exception -> Log.e(TAG, exception.stackTraceToString()) }
                )
        )
    }

    private fun streamsToStreamUIs(streams: List<Stream>): List<StreamUI> = streams.map {
        StreamUI(it, { isChecked, topicUIs, position ->
            toggleStreamItem(isChecked, topicUIs, position)
        }) { topic, streamTitle ->
            openTopicFragment(topic, streamTitle)
        }
    }
}
