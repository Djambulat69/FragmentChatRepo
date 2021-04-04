package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Reaction1
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.myUserName
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

data class MessageUI(
    val message: Message,
    val sender: String,
    override val click: () -> Unit,
    val reactionUpdate: (MutableList<Reaction1>) -> Unit
) :
    ViewTyped {
    override val viewType: Int =
        if (message.senderFullName == myUserName) R.layout.outcoming_message_layout else R.layout.incoming_message_layout
    override val id: String = message.id.toString()
}
