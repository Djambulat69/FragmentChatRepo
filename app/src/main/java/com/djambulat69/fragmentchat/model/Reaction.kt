package com.djambulat69.fragmentchat.model

data class Reaction(
    val emoji: Int,
    var reactionCount: Int,
    var isSet: Boolean = false
)
