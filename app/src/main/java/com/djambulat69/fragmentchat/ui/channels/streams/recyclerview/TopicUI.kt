package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class TopicUI(
    val topic: Topic,
    val streamTitle: String,
    val streamId: Int
) :
    ViewTyped {
    override val id: String = topic.name
    override val viewType: Int = R.layout.topic_list_item
}
