package com.djambulat69.fragmentchat.utils.recyclerView

interface ViewTyped {
    val viewType: Int
    val id: String
    val click: (() -> Unit)?
}
