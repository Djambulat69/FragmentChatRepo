package com.djambulat69.fragmentchat.ui.streams

import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpPresenter

class StreamsPresenter : MvpPresenter<StreamsView>() {

    private val streams = DataBase.streams

    var streamUIs: List<ViewTyped> = streams.map {
        StreamUI(it, { isChecked, topicUIs, position ->
            toggleStreamItem(isChecked, topicUIs, position)
        }) { topic ->
            openTopicFragment(topic)
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showStreams()
    }

    fun openTopicFragment(topic: Topic) {
        viewState.openTopicFragment(topic)
    }

    fun showStreams() {
        viewState.showStreams(streamUIs)
    }

    fun toggleStreamItem(isChecked: Boolean, topicUis: List<TopicUI>, position: Int) {
        viewState.toggleStreamItem(isChecked, topicUis, position)
    }
}
