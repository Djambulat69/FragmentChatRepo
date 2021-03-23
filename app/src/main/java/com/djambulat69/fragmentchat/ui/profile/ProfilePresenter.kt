package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.db.DataBase
import moxy.MvpPresenter

class ProfilePresenter : MvpPresenter<ProfileView>() {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showProfile()
    }

    fun showProfile() {
        viewState.showProfile(DataBase.profile)
    }
}
