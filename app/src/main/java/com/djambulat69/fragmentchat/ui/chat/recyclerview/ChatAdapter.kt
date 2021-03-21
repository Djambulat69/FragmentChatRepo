package com.djambulat69.fragmentchat.ui.chat.recyclerview

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.BaseAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped


class ChatAdapter(holderFactory: ChatHolderFactory, private val commitCallback: Runnable?) :
    BaseAdapter(holderFactory) {

    private val differ = AsyncListDiffer(this, ChatDiffCallback)
    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value, commitCallback)

    object ChatDiffCallback : DiffUtil.ItemCallback<ViewTyped>() {

        override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return when {
                oldItem is MessageUI && newItem is MessageUI ->
                    oldItem.message == newItem.message && oldItem.viewType == newItem.viewType
                oldItem is DateSeparatorUI && newItem is DateSeparatorUI -> oldItem.date == newItem.date
                else -> true
            }
        }
    }
}
