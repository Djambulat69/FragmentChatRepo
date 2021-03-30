package com.djambulat69.fragmentchat.ui.channels

import moxy.MvpView
import moxy.viewstate.strategy.alias.Skip

interface ChannelsView : MvpView {
    @Skip
    fun makeSearch(query: String)
}
