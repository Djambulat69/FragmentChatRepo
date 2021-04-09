package com.djambulat69.fragmentchat.ui.people.recyclerview

import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.UserListItemBinding
import com.djambulat69.fragmentchat.ui.people.Status
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class UserViewHolder(private val binding: UserListItemBinding, private val glide: RequestManager) :
    BaseViewHolder<UserUI>(binding.root) {

    private val activeDrawable = ResourcesCompat.getDrawable(
        binding.root.resources,
        R.drawable.online_icon_active_view_bg,
        binding.root.context.theme
    )

    private val idleDrawable = ResourcesCompat.getDrawable(
        binding.root.resources,
        R.drawable.online_icon_idle_view_bg,
        binding.root.context.theme
    )

    override fun bind(item: UserUI) {
        with(binding) {
            glide.load(item.user.avatarUrl).into(userAvatar)
            userName.text = item.user.fullName
            userEmail.text = item.user.email
            when (item.status) {
                Status.ACTIVE -> {
                    onlineIcon.isVisible = true
                    onlineIcon.background = activeDrawable
                }
                Status.IDLE -> {
                    onlineIcon.isVisible = true
                    onlineIcon.background = idleDrawable
                }
                Status.OFFLINE -> onlineIcon.isVisible = false
            }
        }
    }
}
