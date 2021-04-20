package com.djambulat69.fragmentchat.ui.people.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

object UserDiffCallback : DiffUtil.ItemCallback<ViewTyped>() {

    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return true
    }

}
