package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.network.Stream
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class StreamsResponseSealed {

    @Serializable
    data class AllStreamsResponse(
        @SerialName("msg")
        val msg: String,
        @SerialName("result")
        val result: String,
        @SerialName("streams")
        override val streams: List<Stream>
    ) : StreamsResponseSealed()

    @Serializable
    data class SubscribedStreamsResponse(
        @SerialName("msg")
        val msg: String,
        @SerialName("result")
        val result: String,
        @SerialName("subscriptions")
        override val streams: List<Stream>
    ) : StreamsResponseSealed()

    abstract val streams: List<Stream>
}
