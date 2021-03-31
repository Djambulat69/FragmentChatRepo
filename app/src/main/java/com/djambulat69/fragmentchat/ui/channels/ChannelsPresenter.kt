package com.djambulat69.fragmentchat.ui.channels

import moxy.MvpPresenter

class ChannelsPresenter : MvpPresenter<ChannelsView>() {

    private var lastSearchQuery = ""

    fun searchStreams(query: String) {
        if (query != lastSearchQuery) {
            viewState.makeSearch(query)
            lastSearchQuery = query
        }
    }
}
