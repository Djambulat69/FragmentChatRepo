package com.djambulat69.fragmentchat.ui.chat

import android.view.View
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class DateSeparatorViewHolder(private val dateView: View) :
    BaseViewHolder<DateSeparatorUI>(dateView) {
    val dateTextView = dateView.findViewById<TextView>(R.id.date_separator_text)

    override fun bind(item: DateSeparatorUI) {
        dateTextView.text = item.date
    }
}
