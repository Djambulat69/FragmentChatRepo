package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

object StreamDiffCallback : DiffUtil.ItemCallback<ViewTyped>() {

    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return when {
            oldItem is StreamUI && newItem is StreamUI -> oldItem as StreamUI == newItem as StreamUI && oldItem.isExpanded == newItem.isExpanded
            oldItem is TopicUI && newItem is TopicUI -> oldItem.topic == newItem.topic
            else -> true
        }
    }

}
