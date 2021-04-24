package com.djambulat69.fragmentchat.ui.people.recyclerview

import androidx.recyclerview.widget.DiffUtil

object UserDiffCallback : DiffUtil.ItemCallback<UserUI>() {
    override fun areItemsTheSame(oldItem: UserUI, newItem: UserUI): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserUI, newItem: UserUI): Boolean {
        return true
    }


}
