package com.djambulat69.fragmentchat.ui

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface MainActivityView : MvpView {

    @OneExecution
    fun onNetworkAvailable()

    @OneExecution
    fun onNetworkLost()

}
