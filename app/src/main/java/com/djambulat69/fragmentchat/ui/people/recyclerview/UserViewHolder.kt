package com.djambulat69.fragmentchat.ui.people.recyclerview

import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.databinding.UserListItemBinding
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class UserViewHolder(private val binding: UserListItemBinding, private val glide: RequestManager) :
    BaseViewHolder<UserUI>(binding.root) {
    override fun bind(item: UserUI) {
        with(binding) {
            glide.load(item.user.avatarUrl).into(userAvatar)
            userName.text = item.user.fullName
            userEmail.text = item.user.email
        }
    }
}
