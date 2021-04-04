package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.databinding.IncomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class IncomingMessageViewHolder(
    private val binding: IncomingMessageLayoutBinding,
    private val glide: RequestManager
) :
    BaseViewHolder<MessageUI>(binding.root) {
    override fun bind(item: MessageUI) {
        with(binding.messageViewgroupIncoming) {
            text = item.message.content
            author = item.message.senderFullName
            setOnMessageClickListener(item.click)
            setAddReactionListener(item.click)
            setAvatar(glide, item.message.avatarUrl)
//            setReactions(item.message.reactions, item.reactionUpdate)
        }
    }
}
