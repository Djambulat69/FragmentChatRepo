package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.customUI.setReactions
import com.djambulat69.fragmentchat.databinding.OutcomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class OutComingMessageViewHolder(
    private val binding: OutcomingMessageLayoutBinding
) :
    BaseViewHolder<MessageUI>(binding.root) {

    override fun bind(item: MessageUI) {
        with(binding) {
            messageTextOutcoming.text = item.message.text
            messageTextOutcoming.setOnClickListener(item.click)
            flexboxReactionsOutcoming.setReactions(
                item.id,
                item.message.reactions,
                addReactionButtonOutcoming,
                item.reactionsUpdate
            )
        }
    }
}
