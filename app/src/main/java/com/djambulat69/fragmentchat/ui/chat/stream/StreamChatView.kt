package com.djambulat69.fragmentchat.ui.chat.stream

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface StreamChatView : MvpView {
    @AddToEndSingle
    fun showMessages(uiItems: List<ViewTyped>)

    @AddToEndSingle
    fun showLoading()

    @OneExecution
    fun showEmojiBottomSheet(messageId: Int)

    @OneExecution
    fun showError()

    @OneExecution
    fun showMessageOptions(message: Message)
}
