package com.djambulat69.fragmentchat.ui.chat


import com.djambulat69.fragmentchat.model.Message
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle


interface ChatView : MvpView {
    @AddToEndSingle
    fun showMessages(messages: List<Message>)
}
