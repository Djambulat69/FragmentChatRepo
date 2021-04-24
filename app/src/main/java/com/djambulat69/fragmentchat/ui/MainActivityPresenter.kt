package com.djambulat69.fragmentchat.ui

import android.net.ConnectivityManager
import android.net.Network
import com.djambulat69.fragmentchat.model.network.NetworkChecker
import moxy.MvpPresenter

class MainActivityPresenter : MvpPresenter<MainActivityView>() {

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewState.onNetworkAvailable()
            }
        }
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        NetworkChecker.registerNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        NetworkChecker.unRegisterCallback(networkCallback)

        super.onDestroy()
    }

}
