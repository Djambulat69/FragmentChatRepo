package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class TopicUI(
    val topic: Topic,
    private val streamTitle: String,
    openTopic: (Topic, String) -> Unit
) :
    ViewTyped {
    override val id: String = topic.name
    override val viewType: Int = R.layout.topic_list_item
    override val click: (() -> Unit) = {
        openTopic(topic, streamTitle)
    }
}
