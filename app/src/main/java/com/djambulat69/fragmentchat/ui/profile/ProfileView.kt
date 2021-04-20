package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.network.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : MvpView {
    @AddToEndSingle
    fun showProfile(user: User)

    @AddToEndSingle
    fun showError()

    @AddToEndSingle
    fun showLoading()
}
