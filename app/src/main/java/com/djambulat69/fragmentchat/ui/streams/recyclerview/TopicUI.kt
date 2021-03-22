package com.djambulat69.fragmentchat.ui.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class TopicUI(val topic: Topic, openTopic: (Topic) -> Unit) : ViewTyped {
    override val id: String = topic.title
    override val viewType: Int = R.layout.topic_list_item
    override val click: (() -> Unit) = {
        openTopic(topic)
    }
}
