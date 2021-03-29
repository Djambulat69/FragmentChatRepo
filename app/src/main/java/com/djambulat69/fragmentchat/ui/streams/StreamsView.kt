package com.djambulat69.fragmentchat.ui.streams

import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

interface StreamsView : MvpView {
    @AddToEndSingle
    fun showStreams(streamUIs: List<ViewTyped>)

    @AddToEndSingle
    fun showError()

    @AddToEndSingle
    fun showLoading()

    @Skip
    fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int)

    @Skip
    fun openTopicFragment(topic: Topic, streamTitle: String)
}
