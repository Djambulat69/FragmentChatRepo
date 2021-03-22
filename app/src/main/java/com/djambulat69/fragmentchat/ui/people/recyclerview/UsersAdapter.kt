package com.djambulat69.fragmentchat.ui.people.recyclerview

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.djambulat69.fragmentchat.utils.recyclerView.BaseAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class UsersAdapter(holderFactory: UsersHolderFactory) :
    BaseAdapter(holderFactory) {
    private val differ = AsyncListDiffer(this, UserDiffItemCallback)

    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    object UserDiffItemCallback : DiffUtil.ItemCallback<ViewTyped>() {
        override fun areItemsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ViewTyped, newItem: ViewTyped): Boolean {
            return true
        }
    }
}
