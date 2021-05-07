package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class TopicTitleViewHolder(private val topicTitleTextView: TextView) : BaseViewHolder<TopicTitleUI>(topicTitleTextView) {

    override fun bind(item: TopicTitleUI) {
        topicTitleTextView.text = topicTitleTextView.context.resources.getString(R.string.topic_title, item.topicTitle)
    }
}
