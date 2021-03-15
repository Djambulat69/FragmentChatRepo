package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.databinding.IncomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class IncomingMessageViewHolder(private val binding: IncomingMessageLayoutBinding) :
    BaseViewHolder<MessageUI>(binding.root) {
    override fun bind(item: MessageUI) {
        binding.messageViewgroupIncoming.text = item.message.text
        binding.messageViewgroupIncoming.author = item.message.author
    }
}
