package com.djambulat69.fragmentchat.ui.people

import com.djambulat69.fragmentchat.model.User
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface PeopleView : MvpView {
    @AddToEndSingle
    fun showUsers(users: List<User>)
}
