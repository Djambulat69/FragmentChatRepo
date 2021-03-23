package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.view.View
import android.widget.TextView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.BaseViewHolder

class DateSeparatorViewHolder(dateView: View) :
    BaseViewHolder<DateSeparatorUI>(dateView) {
    private val dateTextView: TextView = dateView.findViewById(R.id.date_separator_text)

    override fun bind(item: DateSeparatorUI) {
        dateTextView.text = item.date
    }
}
