package com.djambulat69.fragmentchat.ui.streams

import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamUI
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpPresenter

class StreamsPresenter : MvpPresenter<StreamsView>() {

    var streamUIs: List<ViewTyped> = DataBase.streams.map {
        StreamUI(it) { isChecked, topicUIs, position ->
            toggleStreamItem(isChecked, topicUIs, position)
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showStreams()
    }

    fun showStreams() {
        viewState.showStreams(streamUIs)
    }

    fun toggleStreamItem(isChecked: Boolean, topicUis: List<TopicUI>, position: Int) {
        viewState.toggleStreamItem(isChecked, topicUis, position)
    }
}
