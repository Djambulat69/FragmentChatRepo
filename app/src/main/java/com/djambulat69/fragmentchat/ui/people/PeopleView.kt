package com.djambulat69.fragmentchat.ui.people

import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

interface PeopleView : MvpView {
    @AddToEndSingle
    fun showUsers(userUIs: List<UserUI>)

    @AddToEndSingle
    fun setError(visible: Boolean)

    @AddToEndSingle
    fun setLoading(visible: Boolean)
}
