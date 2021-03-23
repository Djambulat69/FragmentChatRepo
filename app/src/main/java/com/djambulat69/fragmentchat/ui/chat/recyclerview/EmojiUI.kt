package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class EmojiUI(val emojiCode: Int, emojiClickCallback: (Int) -> Unit) : ViewTyped {

    override val id: String = emojiCode.toString()
    override val viewType: Int = R.layout.emoji_grid_item
    override val click: (() -> Unit) = {
        emojiClickCallback(emojiCode)
    }
}
