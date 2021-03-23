package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.widget.TextView
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.toEmoji

class EmojiViewHolder(private val emojiTextView: TextView) : BaseViewHolder<EmojiUI>(emojiTextView) {

    override fun bind(item: EmojiUI) {
        emojiTextView.text = item.emojiCode.toEmoji()
        emojiTextView.setOnClickListener {
            item.click()
        }
    }
}
