package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ClickMapper
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamsClickMapper : ClickMapper<StreamsClickTypes>() {

    override fun map(itemClick: ItemClick, items: List<ViewTyped>): StreamsClickTypes =
        when (itemClick.view.id) {
            R.id.stream_list_item -> StreamsClickTypes.StreamClick(
                items[itemClick.position] as StreamUI,
                itemClick.position,
                itemClick.view.findViewById(R.id.checked_image)
            )
            R.id.topic_list_item -> StreamsClickTypes.TopicClick(items[itemClick.position] as TopicUI)
            R.id.open_stream_button -> StreamsClickTypes.OpenStreamClick(items[itemClick.position] as StreamUI)
            else -> throw IllegalStateException("Unknown viewId of itemClick: $itemClick")
        }
}
