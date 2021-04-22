package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.EmojiEnum
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class EmojiUI(val emoji: EmojiEnum, emojiClickCallback: (String) -> Unit) : ViewTyped {

    override val id: String = emoji.unicode
    override val viewType: Int = R.layout.emoji_grid_item
    /*override val click: (() -> Unit) = {
        emojiClickCallback(emoji.nameInZulip)
    }
*/
}
