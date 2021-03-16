package com.djambulat69.fragmentchat.ui.chat

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.BaseAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped


class ChatAdapter(holderFactory: ChatHolderFactory) : BaseAdapter(holderFactory) {
    private val differ = AsyncListDiffer(this, ChatDiffCallback)
    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    object ChatDiffCallback : DiffUtil.ItemCallback<ViewTyped>() {

        override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return if (oldItem is MessageUI && newItem is MessageUI) {
                val a = oldItem.message == newItem.message
                oldItem.message == newItem.message && oldItem.viewType == newItem.viewType
            } else false
        }

    }
}
