package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface StreamsView : MvpView {

    @AddToEndSingle
    fun showStreams(streamUIs: List<ViewTyped>)

    @AddToEndSingle
    fun setError(visible: Boolean)

    @OneExecution
    fun showToastError()

    @AddToEndSingle
    fun setLoading(visible: Boolean)

    @OneExecution
    fun openTopicFragment(topicTitle: String, streamTitle: String, streamId: Int)

    @OneExecution
    fun openStreamFragment(streamTitle: String, streamId: Int)

}
