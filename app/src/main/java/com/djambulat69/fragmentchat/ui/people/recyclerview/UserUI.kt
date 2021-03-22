package com.djambulat69.fragmentchat.ui.people.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.User
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class UserUI(
    val user: User
) : ViewTyped {
    override val viewType: Int = R.layout.user_list_item
    override val id: String = user.userId.toString()
    override val click: (() -> Unit)? = null
}
