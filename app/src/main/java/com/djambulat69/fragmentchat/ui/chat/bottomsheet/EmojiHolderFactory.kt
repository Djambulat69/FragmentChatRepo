package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.HolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class EmojiHolderFactory : HolderFactory() {

    override fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.emoji_grid_item -> EmojiViewHolder(view as TextView, clicks)
            else -> throw Exception("Unknown viewType: $viewType")
        } as BaseViewHolder<ViewTyped>
    }
}
