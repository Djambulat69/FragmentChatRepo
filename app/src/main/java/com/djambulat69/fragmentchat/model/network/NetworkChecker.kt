package com.djambulat69.fragmentchat.model.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.djambulat69.fragmentchat.ui.FragmentChatApplication

object NetworkChecker {

    fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val networkRequest = NetworkRequest.Builder().build()
        getConnectivityManager().registerNetworkCallback(networkRequest, callback)
    }

    fun unRegisterCallback(callback: ConnectivityManager.NetworkCallback) {
        getConnectivityManager().unregisterNetworkCallback(callback)
    }

    fun isConnected(): Boolean {
        val connectivityManager = getConnectivityManager()

        for (network in connectivityManager.allNetworks) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                return true
            }
        }
        return false
    }

    private fun getConnectivityManager() =
        FragmentChatApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}
