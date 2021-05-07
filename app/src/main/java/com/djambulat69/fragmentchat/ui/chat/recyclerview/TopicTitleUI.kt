package com.djambulat69.fragmentchat.ui.chat.recyclerview

import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class TopicTitleUI(val topicTitle: String) : ViewTyped {

    override val id: String = topicTitle
    override val viewType: Int = R.layout.topic_title_view

}
