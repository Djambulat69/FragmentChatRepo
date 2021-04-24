package com.djambulat69.fragmentchat.ui.channels

import moxy.MvpPresenter
import javax.inject.Inject

class ChannelsPresenter @Inject constructor() : MvpPresenter<ChannelsView>() {

    private var lastSearchQuery = ""

    fun searchStreams(query: String) {
        if (query != lastSearchQuery) {
            viewState.makeSearch(query)
            lastSearchQuery = query
        }
    }
}
