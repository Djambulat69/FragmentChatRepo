package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import androidx.recyclerview.widget.DiffUtil

object EmojiDiffCallback : DiffUtil.ItemCallback<EmojiUI>() {

    override fun areItemsTheSame(oldItem: EmojiUI, newItem: EmojiUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EmojiUI, newItem: EmojiUI): Boolean {
        return true
    }

}
