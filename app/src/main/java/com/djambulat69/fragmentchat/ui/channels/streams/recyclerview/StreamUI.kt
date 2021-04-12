package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

data class StreamUI(
    val stream: Stream,
    val expand: (Boolean, List<TopicUI>, Int) -> Unit,
    val openTopic: (Topic) -> Unit
) : ViewTyped {

    override val viewType: Int = R.layout.stream_list_item
    override val id: String = stream.name
    override val click: () -> Unit = { isExpanded = !isExpanded }

    private val childTopicUIs: List<TopicUI> = stream.topics.map { topic -> TopicUI(topic, openTopic) }

    var isExpanded = false

    val clickWithPosition: (Int) -> Unit = { position ->
        click()
        expand(isExpanded, childTopicUIs, position)
    }
}
