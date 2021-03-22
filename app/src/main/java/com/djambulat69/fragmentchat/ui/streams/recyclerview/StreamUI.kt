package com.djambulat69.fragmentchat.ui.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamUI(
    val stream: Stream,
    expand: (Boolean, List<TopicUI>, Int) -> Unit,
    openTopic: (Topic) -> Unit
) : ViewTyped {
    override val viewType: Int = R.layout.stream_list_item
    override val id: String = stream.title
    private val childTopics = stream.topics.map { topic -> TopicUI(topic, openTopic) }
    var isExpanded = false
    val clickWithPosition: (Int) -> Unit = { position ->
        click()
        expand(isExpanded, childTopics, position)
    }
    override val click: () -> Unit = {
        isExpanded = !isExpanded
    }
}
