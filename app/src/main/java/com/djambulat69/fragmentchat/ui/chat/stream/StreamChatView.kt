package com.djambulat69.fragmentchat.ui.chat.stream

import com.djambulat69.fragmentchat.ui.chat.BaseChatView
import moxy.viewstate.strategy.alias.OneExecution

interface StreamChatView : BaseChatView {

    @OneExecution
    fun openTopicChat(topicTitle: String)

}
