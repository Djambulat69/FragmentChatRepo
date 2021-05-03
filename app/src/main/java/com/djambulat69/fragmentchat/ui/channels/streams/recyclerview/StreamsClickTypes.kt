package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import android.widget.ImageView
import com.djambulat69.fragmentchat.utils.recyclerView.ClickTypes

sealed class StreamsClickTypes : ClickTypes() {
    class StreamClick(val streamUI: StreamUI, val position: Int, val arrowImageView: ImageView) : StreamsClickTypes()
    class TopicClick(val topicUI: TopicUI) : StreamsClickTypes()
    class OpenStreamClick(val streamUI: StreamUI) : StreamsClickTypes()
}
