package com.djambulat69.fragmentchat.ui.streams

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamUI(val stream: Stream, expand: (Boolean, List<TopicUI>, Int) -> Unit) : ViewTyped {
    override val viewType: Int = R.layout.stream_list_item
    override val id: String = stream.title
    val childTopics = stream.topics.map { topic -> TopicUI(topic, null) }
    var isChecked = false
    val clickWithPosition: (Int) -> Unit = { position ->
        click()
        expand(isChecked, childTopics, position)
    }
    override val click: () -> Unit = {
        isChecked = !isChecked
    }
}
