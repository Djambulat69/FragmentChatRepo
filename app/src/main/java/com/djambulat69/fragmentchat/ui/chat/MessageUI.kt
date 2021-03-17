package com.djambulat69.fragmentchat.ui.chat

import android.view.View
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class MessageUI(
    val message: Message,
    sender: String,
    override val click: View.OnLongClickListener,
    val reactionUpdate: (MutableList<Reaction>) -> Unit
) :
    ViewTyped {
    override val viewType: Int =
        if (sender == message.author)
            R.layout.outcoming_message_layout
        else
            R.layout.incoming_message_layout
    override val id: String = message.id
}
