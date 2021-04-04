package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.network.OwnUser
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : MvpView {
    @AddToEndSingle
    fun showProfile(user: OwnUser)

    @AddToEndSingle
    fun showError()

    @AddToEndSingle
    fun showLoading()
}
