package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import android.widget.TextView
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class EmojiViewHolder(private val emojiTextView: TextView, private val clicks: PublishRelay<ItemClick>) :
    BaseViewHolder<EmojiUI>(emojiTextView) {

    init {
        emojiTextView.setOnClickListener {
            clicks.accept(ItemClick(bindingAdapterPosition, it))
        }
    }

    override fun bind(item: EmojiUI) {
        emojiTextView.text = item.emoji.unicode
    }
}
