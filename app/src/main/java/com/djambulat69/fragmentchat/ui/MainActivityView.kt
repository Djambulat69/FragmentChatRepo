package com.djambulat69.fragmentchat.ui

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface MainActivityView : MvpView {

    @AddToEndSingle
    fun onNetwork(isAvailable: Boolean)

}
