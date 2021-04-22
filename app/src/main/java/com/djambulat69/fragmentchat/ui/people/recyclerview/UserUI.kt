package com.djambulat69.fragmentchat.ui.people.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.PresenceClient
import com.djambulat69.fragmentchat.model.network.User
import com.djambulat69.fragmentchat.ui.people.Status
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import java.util.*

class UserUI(
    val user: User,
    private val presence: PresenceClient
) : ViewTyped {

    override val viewType: Int = R.layout.user_list_item
    override val id: String = user.userId.toString()

    val status: Status
        get() = when (presence.status.toUpperCase(Locale.ROOT)) {
            Status.ACTIVE.name -> Status.ACTIVE
            Status.IDLE.name -> Status.IDLE
            Status.OFFLINE.name -> Status.OFFLINE
            else -> throw IllegalStateException("Unknown user status:$user;${presence.status}")
        }
}
