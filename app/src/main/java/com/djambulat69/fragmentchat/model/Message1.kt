package com.djambulat69.fragmentchat.model


data class Message1(
    val id: String,
    val text: String,
    val author: String,
    var reactions: MutableList<Reaction1>,
    val date: String
)
