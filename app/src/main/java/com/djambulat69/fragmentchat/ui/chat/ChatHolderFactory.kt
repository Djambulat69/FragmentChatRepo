package com.djambulat69.fragmentchat.ui.chat

import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.IncomingMessageLayoutBinding
import com.djambulat69.fragmentchat.databinding.OutcomingMessageLayoutBinding
import com.djambulat69.fragmentchat.utils.inflate
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.HolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class ChatHolderFactory : HolderFactory() {
    override fun createHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewTyped> {
        val view = parent.inflate<View>(viewType)
        return when (viewType) {
            R.layout.incoming_message_layout -> {
                val binding = IncomingMessageLayoutBinding.bind(view)
                IncomingMessageViewHolder(binding)
            }
            R.layout.outcoming_message_layout -> {
                val binding = OutcomingMessageLayoutBinding.bind(view)
                OutComingMessageViewHolder(binding)
            }
            R.layout.date_separator -> DateSeparatorViewHolder(view)
            else ->
                throw Exception("Unknown ViewType ${parent.resources.getResourceName(viewType)}")
        } as BaseViewHolder<ViewTyped>
    }
}
