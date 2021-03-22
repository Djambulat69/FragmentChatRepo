package com.djambulat69.fragmentchat.ui.people

import com.djambulat69.fragmentchat.model.db.DataBase
import moxy.MvpPresenter

class PeoplePresenter : MvpPresenter<PeopleView>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showUsers()
    }

    fun showUsers() {
        viewState.showUsers(DataBase.users)
    }
}
