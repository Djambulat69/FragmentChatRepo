package com.djambulat69.fragmentchat.model

data class Stream(
    val title: String,
    val topics: List<Topic>,
    val isSubscribed: Boolean
)
