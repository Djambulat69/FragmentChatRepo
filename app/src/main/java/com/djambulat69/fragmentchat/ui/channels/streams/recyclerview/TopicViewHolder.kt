package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import android.view.View
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class TopicViewHolder(topicView: View, private val clicks: PublishRelay<ItemClick>) : BaseViewHolder<TopicUI>(topicView) {

    private val topicTitleTextView: TextView = topicView.findViewById(R.id.topic_title)
    private val messagesCount: TextView = topicView.findViewById(R.id.topic_messages_count)

    init {
        topicView.setOnClickListener { clicks.accept(ItemClick(bindingAdapterPosition, it)) }
    }

    override fun bind(item: TopicUI) {
        topicTitleTextView.text = item.topic.name
        messagesCount.text = item.topic.maxId.toString()
    }
}
