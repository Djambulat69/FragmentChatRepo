package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.MyUser
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.secondsToDateString

class MessageUI(val message: Message) : ViewTyped {

    override val viewType: Int =
        if (message.senderId == MyUser.getId()) R.layout.outcoming_message_layout else R.layout.incoming_message_layout
    override val id: String = message.id.toString()

    val date: String = secondsToDateString(message.timestamp.toLong())
}
