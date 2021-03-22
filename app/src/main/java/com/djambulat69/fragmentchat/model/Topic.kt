package com.djambulat69.fragmentchat.model

import java.io.Serializable

data class Topic(
    val title: String,
    val messagesCount: Int
) : Serializable
