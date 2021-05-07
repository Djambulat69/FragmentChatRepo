package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.HolderFactory

class EmojiHolderFactory : HolderFactory<EmojiUI>() {

    override fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<EmojiUI> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.emoji_grid_item -> EmojiViewHolder(view as TextView, clicks)
            else -> throw Exception("Unknown viewType: $viewType")
        }
    }
}
