package com.djambulat69.fragmentchat.ui.people.recyclerview

import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.UserListItemBinding
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.HolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class UsersHolderFactory(private val glide: RequestManager) : HolderFactory() {

    override fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.user_list_item -> {
                val binding = UserListItemBinding.bind(view)
                UserViewHolder(binding, glide)
            }
            else -> throw IllegalStateException("Unknown viewtype: $viewType")
        } as BaseViewHolder<ViewTyped>
    }

}
