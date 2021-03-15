package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class MessageUI(val message: Message, sender: String) : ViewTyped {
    override val viewType: Int =
        if (sender == message.author)
            R.layout.outcoming_message_layout
        else
            R.layout.incoming_message_layout
    override val id: Long = message.id
}
