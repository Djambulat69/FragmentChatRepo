package com.djambulat69.fragmentchat.ui.streams.recyclerview

import android.view.View
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class TopicViewHolder(private val topicView: View) : BaseViewHolder<TopicUI>(topicView) {
    private val topicTitleTextView: TextView = topicView.findViewById(R.id.topic_title)
    private val messagesCount: TextView = topicView.findViewById(R.id.topic_messages_count)

    override fun bind(item: TopicUI) {
        topicTitleTextView.text = item.topic.title
        messagesCount.text = item.topic.messagesCount.toString()
        topicView.setOnClickListener {
            item.click()
        }
    }
}
