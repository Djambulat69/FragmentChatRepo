package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.widget.TextView
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class EmojiViewHolder(private val emojiTextView: TextView) : BaseViewHolder<EmojiUI>(emojiTextView) {

    var currentItemClick: (() -> Unit)? = null

    init {
        emojiTextView.setOnClickListener {
            currentItemClick?.invoke()
        }
    }

    override fun bind(item: EmojiUI) {
        emojiTextView.text = item.emoji.unicode
        currentItemClick = item.click
    }
}
