package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.utils.recyclerView.ClickTypes

sealed class ChatClickTypes : ClickTypes() {
    class AddEmojiClick(val item: MessageUI) : ChatClickTypes()
    class ReactionClick(val messageId: Int, val emojiName: String, val isSelected: Boolean) : ChatClickTypes()
}
