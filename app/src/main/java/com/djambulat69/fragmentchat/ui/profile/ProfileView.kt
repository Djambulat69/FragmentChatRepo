package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.Profile
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface ProfileView : MvpView {
    @AddToEndSingle
    fun showProfile(profile: Profile)
}
