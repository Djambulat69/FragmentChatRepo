package com.djambulat69.fragmentchat.model

data class Reaction(
    val emoji: Int,
    val reactionCount: Int,
    var isSet: Boolean = false
)
