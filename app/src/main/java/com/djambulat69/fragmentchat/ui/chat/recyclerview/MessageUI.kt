package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.myUserId
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.secondsToDateString

class MessageUI(
    val message: Message,
    override val click: () -> Unit,
    val reactionClick: (Boolean, Int, String) -> Unit
) :
    ViewTyped {

    override val viewType: Int =
        if (message.senderId == myUserId) R.layout.outcoming_message_layout else R.layout.incoming_message_layout
    override val id: String = message.id.toString()

    val date: String = secondsToDateString(message.timestamp.toLong())
}
