package com.djambulat69.fragmentchat.ui.streams

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class StreamViewHolder(val streamView: View) : BaseViewHolder<StreamUI>(streamView) {

    private val streamTitleTextView = streamView.findViewById<TextView>(R.id.stream_title)
    private val streamExpandImageView = streamView.findViewById<ImageView>(R.id.checked_image)

    override fun bind(item: StreamUI) {
        streamTitleTextView.text = item.stream.title
        streamView.setOnClickListener {
            item.clickWithPosition(adapterPosition)
            streamExpandImageView.setImageResource(
                if (item.isChecked)
                    R.drawable.ic_baseline_keyboard_arrow_up_24
                else
                    R.drawable.ic_baseline_keyboard_arrow_down_24
            )
        }
    }
}
