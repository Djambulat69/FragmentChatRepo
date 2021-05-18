package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.text.method.LinkMovementMethod
import android.view.View
import com.djambulat69.fragmentchat.customUI.setReactions
import com.djambulat69.fragmentchat.databinding.OutcomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.parseHtml
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class OutComingMessageViewHolder(
    private val binding: OutcomingMessageLayoutBinding,
    private val clicks: PublishRelay<ItemClick>
) :
    BaseViewHolder<MessageUI>(binding.root) {

    private val clickFunction = { view: View -> clicks.accept(ItemClick(bindingAdapterPosition, view)) }

    init {
        with(binding) {
            outcomingMessageLayout.setOnLongClickListener { clickFunction(it); true }
            addReactionButtonOutcoming.root.setOnClickListener(clickFunction)
            messageTextOutcoming.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun bind(item: MessageUI) {
        with(binding) {
            messageTextOutcoming.text = parseHtml(item.message.content)
            flexboxReactionsOutcoming.setReactions(
                item.message.reactions.sortedBy { it.emojiName },
                clickFunction
            )
        }
    }

}
