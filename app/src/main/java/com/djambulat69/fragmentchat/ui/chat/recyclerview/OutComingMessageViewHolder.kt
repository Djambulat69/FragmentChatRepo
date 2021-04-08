package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.customUI.setReactions
import com.djambulat69.fragmentchat.databinding.OutcomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class OutComingMessageViewHolder(
    private val binding: OutcomingMessageLayoutBinding
) :
    BaseViewHolder<MessageUI>(binding.root) {

    override fun bind(item: MessageUI) {
        with(binding) {
            messageTextOutcoming.text = item.message.content
            messageTextOutcoming.setOnLongClickListener {
                item.click()
                true
            }
            addReactionButtonOutcoming.setOnClickListener {
                item.click()
            }
            flexboxReactionsOutcoming.setReactions(
                item.message.reactions,
                item.reactionUpdate
            )
        }
    }
}
