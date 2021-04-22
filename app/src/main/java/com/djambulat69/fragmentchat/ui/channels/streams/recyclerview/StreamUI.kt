package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

data class StreamUI(
    val stream: Stream
) : ViewTyped {

    override val viewType: Int = R.layout.stream_list_item
    override val id: String = stream.name

    val childTopicUIs: List<TopicUI> = stream.topics.map { topic -> TopicUI(topic, stream.name, stream.streamId) }

    var isExpanded = false

}
