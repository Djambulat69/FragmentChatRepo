package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.view.View
import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.databinding.IncomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class IncomingMessageViewHolder(
    private val binding: IncomingMessageLayoutBinding,
    private val glide: RequestManager,
    private val clicks: PublishRelay<ItemClick>
) :
    BaseViewHolder<MessageUI>(binding.root) {

    private val clickFunction = { view: View -> clicks.accept(ItemClick(bindingAdapterPosition, view)) }

    init {
        with(binding.messageViewgroupIncoming) {
            setOnMessageClickListener(clickFunction)
            setAddReactionListener(clickFunction)
        }
    }

    override fun bind(item: MessageUI) {
        with(binding.messageViewgroupIncoming) {
            text = item.message.content
            author = item.message.senderFullName
            setReactions(item.message.reactions.sortedBy { it.emojiName }, clickFunction, item.message.id)
            setAvatar(glide, item.message.avatarUrl)
        }
    }
}
