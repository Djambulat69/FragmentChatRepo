package com.djambulat69.fragmentchat.ui.channels

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface ChannelsView : MvpView {
    @OneExecution
    fun makeSearch(query: String)

    @OneExecution
    fun showStreamCreatedSnackbar()

    @OneExecution
    fun showError()

    @OneExecution
    fun updateStreams()
}
