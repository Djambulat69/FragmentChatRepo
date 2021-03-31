package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

data class StreamUI(
    val stream: Stream,
    private val expand: (Boolean, List<TopicUI>, Int) -> Unit,
    private val openTopic: (Topic, String) -> Unit
) : ViewTyped {

    override val viewType: Int = R.layout.stream_list_item
    override val id: String = stream.title
    override val click: () -> Unit = {
        isExpanded = !isExpanded
    }

    private val childTopics = stream.topics.map { topic -> TopicUI(topic, stream.title, openTopic) }
    var isExpanded = false

    val clickWithPosition: (Int) -> Unit = { position ->
        click()
        expand(isExpanded, childTopics, position)
    }

}
