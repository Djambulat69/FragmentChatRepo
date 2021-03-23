package com.djambulat69.fragmentchat.ui.people.recyclerview

import com.djambulat69.fragmentchat.databinding.UserListItemBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class UserViewHolder(private val binding: UserListItemBinding) :
    BaseViewHolder<UserUI>(binding.root) {
    override fun bind(item: UserUI) {
        with(binding) {
            userName.text = item.user.userName
            userEmail.text = item.user.email
        }
    }
}