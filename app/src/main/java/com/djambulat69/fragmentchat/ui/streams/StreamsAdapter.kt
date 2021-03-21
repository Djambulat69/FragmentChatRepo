package com.djambulat69.fragmentchat.ui.streams

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamsAdapter : ListAdapter<ViewTyped, BaseViewHolder<ViewTyped>>(StreamDiffItemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.stream_list_item -> StreamViewHolder(view)
            R.layout.topic_list_item -> TopicViewHolder(view)
            else -> throw Exception("Unknown ViewType: $viewType")
        } as BaseViewHolder<ViewTyped>
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ViewTyped>, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = getItem(position).viewType
}

object StreamDiffItemCallback : DiffUtil.ItemCallback<ViewTyped>() {
    override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
        return when {
            oldItem is StreamUI && newItem is StreamUI -> oldItem.stream == newItem.stream
            oldItem is TopicUI && newItem is TopicUI -> oldItem.topic == newItem.topic
            else -> true
        }
    }

}
