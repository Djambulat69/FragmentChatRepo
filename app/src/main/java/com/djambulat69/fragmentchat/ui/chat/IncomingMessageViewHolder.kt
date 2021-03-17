package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.databinding.IncomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class IncomingMessageViewHolder(
    private val binding: IncomingMessageLayoutBinding,
) :
    BaseViewHolder<MessageUI>(binding.root) {
    override fun bind(item: MessageUI) {
        with(binding.messageViewgroupIncoming)
        {
            text = item.message.text
            author = item.message.author
            setOnMessageClickListener(item.click)
            setAddReactionLister(item.click)
            setReactions(item.message.reactions, item.reactionUpdate)
        }
    }

}
