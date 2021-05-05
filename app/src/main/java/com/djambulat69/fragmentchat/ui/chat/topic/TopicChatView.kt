package com.djambulat69.fragmentchat.ui.chat.topic

import com.djambulat69.fragmentchat.model.network.Message
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution


interface TopicChatView : MvpView {
    @AddToEndSingle
    fun showMessages(messages: List<Message>)

    @AddToEndSingle
    fun showLoading()

    @OneExecution
    fun showEmojiBottomSheet(messageId: Int)

    @OneExecution
    fun showError()

    @OneExecution
    fun showMessageOptions()
}
