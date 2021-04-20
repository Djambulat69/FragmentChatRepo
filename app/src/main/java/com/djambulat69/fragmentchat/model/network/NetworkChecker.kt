package com.djambulat69.fragmentchat.model.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.djambulat69.fragmentchat.ui.FragmentChatApplication

object NetworkChecker {


    fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        val connectivityManager =
            FragmentChatApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, callback)
    }

    fun isConnected(): Boolean {
        val connectivityManager =
            FragmentChatApplication.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        for (network in connectivityManager.allNetworks) {
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true) {
                return true
            }
        }
        return false
    }
}
