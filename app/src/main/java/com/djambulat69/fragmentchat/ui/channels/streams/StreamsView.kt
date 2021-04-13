package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface StreamsView : MvpView {

    @AddToEndSingle
    fun showStreams(streamUIs: List<ViewTyped>)

    @AddToEndSingle
    fun showError()

    @OneExecution
    fun showToastError()

    @AddToEndSingle
    fun showLoading()

    @Skip
    fun openTopicFragment(topic: Topic, streamTitle: String, streamId: Int)

}
