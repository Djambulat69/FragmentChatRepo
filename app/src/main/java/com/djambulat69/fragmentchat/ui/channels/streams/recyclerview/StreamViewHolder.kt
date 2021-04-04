package com.djambulat69.fragmentchat.ui.channels.streams.recyclerview

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class StreamViewHolder(private val streamView: View) : BaseViewHolder<StreamUI>(streamView) {

    private val streamTitleTextView = streamView.findViewById<TextView>(R.id.stream_title)
    private val streamExpandImageView = streamView.findViewById<ImageView>(R.id.checked_image)

    override fun bind(item: StreamUI) {
        streamTitleTextView.text =
            streamView.context.getString(R.string.sharp_placeholder, item.stream.name)
        setExpandImage(item.isExpanded)
        streamView.setOnClickListener {
            item.clickWithPosition(adapterPosition)
            setExpandImage(item.isExpanded)
        }
    }

    private fun setExpandImage(isExpanded: Boolean) {
        streamExpandImageView.setImageResource(
            if (isExpanded)
                R.drawable.ic_baseline_keyboard_arrow_up_24
            else
                R.drawable.ic_baseline_keyboard_arrow_down_24
        )
    }
}
