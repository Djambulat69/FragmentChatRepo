package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface ChatView : MvpView {

    @AddToEndSingle
    fun showMessages(uiItems: List<ViewTyped>)

    @AddToEndSingle
    fun setLoading(visible: Boolean)

    @AddToEndSingle
    fun setFileLoading(visible: Boolean)

    @OneExecution
    fun showEmojiBottomSheet(messageId: Int)

    @OneExecution
    fun showError()

    @OneExecution
    fun showMessageOptions(message: Message)

    @OneExecution
    fun attachUriToMessage(uri: String)

}
