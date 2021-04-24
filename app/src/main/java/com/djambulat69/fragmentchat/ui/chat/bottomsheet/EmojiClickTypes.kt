package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import com.djambulat69.fragmentchat.utils.recyclerView.ClickTypes

sealed class EmojiClickTypes : ClickTypes() {
    class EmojiClick(val emojiUI: EmojiUI) : EmojiClickTypes()
}
