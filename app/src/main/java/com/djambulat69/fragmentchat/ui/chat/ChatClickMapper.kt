package com.djambulat69.fragmentchat.ui.chat

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.customUI.EmojiView
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import com.djambulat69.fragmentchat.utils.recyclerView.ClickMapper
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class ChatClickMapper : ClickMapper<ChatClickTypes>() {

    override fun map(itemClick: ItemClick, items: List<ViewTyped>): ChatClickTypes =
        when (itemClick.view.id) {

            R.id.message_text_outcoming,
            R.id.message_linear_layout,
            R.id.add_reaction_button_outcoming -> ChatClickTypes.AddEmojiClick(items[itemClick.position] as MessageUI)

            R.id.emoji_view -> ChatClickTypes.ReactionClick(
                (items[itemClick.position] as MessageUI).message.id,
                (itemClick.view as EmojiView).emojiName,
                itemClick.view.isSelected
            )

            else -> throw IllegalArgumentException("Unknow viewId of itemClick: $itemClick")
        }

}
