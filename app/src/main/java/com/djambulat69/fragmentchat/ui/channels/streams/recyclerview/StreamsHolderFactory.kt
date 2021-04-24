package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.HolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class StreamsHolderFactory : HolderFactory<ViewTyped>() {
    override fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.stream_list_item -> StreamViewHolder(view, clicks)
            R.layout.topic_list_item -> TopicViewHolder(view, clicks)
            else -> throw Exception("Unknown ViewType: $viewType")
        } as BaseViewHolder<ViewTyped>
    }
}
