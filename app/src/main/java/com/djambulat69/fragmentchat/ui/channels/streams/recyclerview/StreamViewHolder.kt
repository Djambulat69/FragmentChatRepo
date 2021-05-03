package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder
import com.djambulat69.fragmentchat.utils.recyclerView.ItemClick
import com.jakewharton.rxrelay3.PublishRelay

class StreamViewHolder(private val streamView: View, private val clicks: PublishRelay<ItemClick>) :
    BaseViewHolder<StreamUI>(streamView) {

    private val streamTitleTextView = streamView.findViewById<TextView>(R.id.stream_title)
    private val streamExpandImageView = streamView.findViewById<ImageView>(R.id.checked_image)
    private val openStreamButton = streamView.findViewById<Button>(R.id.open_stream_button)

    init {
        streamView.setOnClickListener {
            clicks.accept(ItemClick(bindingAdapterPosition, it))
        }
        openStreamButton.setOnClickListener {
            clicks.accept(ItemClick(bindingAdapterPosition, it))
        }
    }

    override fun bind(item: StreamUI) {
        streamTitleTextView.text = streamView.context.getString(R.string.sharp_placeholder, item.stream.name)
        setArrowImage(item.isExpanded)
    }

    private fun setArrowImage(isExpanded: Boolean) {
        streamExpandImageView.setImageResource(
            if (isExpanded)
                R.drawable.ic_baseline_keyboard_arrow_up_24
            else
                R.drawable.ic_baseline_keyboard_arrow_down_24
        )
    }
}
