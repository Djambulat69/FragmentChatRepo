package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ClickMapper
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class EmojiClickMapper : ClickMapper<EmojiClickTypes>() {

    override fun map(itemClick: ItemClick, items: List<ViewTyped>): EmojiClickTypes =
        when (itemClick.view.id) {
            R.id.emoji_grid_item -> EmojiClickTypes.EmojiClick(items[itemClick.position] as EmojiUI)
            else -> throw IllegalArgumentException("Unknown viewId of itemClick: $itemClick")
        }

}
