package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.BaseAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamsAdapter(holderFactory: StreamsHolderFactory) : BaseAdapter(holderFactory) {

    private val differ = AsyncListDiffer(this, StreamDiffItemCallback)

    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    object StreamDiffItemCallback : DiffUtil.ItemCallback<ViewTyped>() {
        override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return when {
                oldItem is StreamUI && newItem is StreamUI -> oldItem as StreamUI == newItem as StreamUI
                oldItem is TopicUI && newItem is TopicUI -> oldItem.topic == newItem.topic
                else -> true
            }
        }

    }
}
