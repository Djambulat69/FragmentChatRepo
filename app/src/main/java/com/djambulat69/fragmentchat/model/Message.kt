package com.djambulat69.fragmentchat.model

data class Message(
    val id: Long,
    val text: String,
    val author: String,
    var reactions: MutableList<Reaction>
)
