package com.djambulat69.fragmentchat.ui.chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.customUI.MessageViewGroup
import com.djambulat69.fragmentchat.model.Message

class ChatRecyclerAdapter(private var messages: List<Message>) :
    RecyclerView.Adapter<ChatRecyclerAdapter.MessageViewHolder>() {
    class MessageViewHolder(private val view: MessageViewGroup) : RecyclerView.ViewHolder(view) {
        init {

        }

        fun bind(msg: Message) {
            view.setReactions(msg.reactions)
            view.setMessage(msg.author, msg.text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = MessageViewGroup(parent.context)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    fun setMessages(messages: List<Message>) {
        this.messages = messages
        notifyItemInserted(0)
    }
}
