package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.databinding.OutcomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class OutComingMessageViewHolder(private val binding: OutcomingMessageLayoutBinding) :
    BaseViewHolder<MessageUI>(binding.root) {
    override fun bind(item: MessageUI) {
        binding.messageTextOutcoming.text = item.message.text
    }
}
