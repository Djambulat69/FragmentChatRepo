package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class TopicTitleViewHolder(private val topicTitleTextView: TextView, private val clicks: PublishRelay<ItemClick>) :
    BaseViewHolder<TopicTitleUI>(topicTitleTextView) {

    init {
        topicTitleTextView.setOnClickListener {
            clicks.accept(ItemClick(bindingAdapterPosition, it))
        }
    }

    override fun bind(item: TopicTitleUI) {
        topicTitleTextView.text = topicTitleTextView.context.resources.getString(R.string.topic_title, item.topicTitle)
    }
}
