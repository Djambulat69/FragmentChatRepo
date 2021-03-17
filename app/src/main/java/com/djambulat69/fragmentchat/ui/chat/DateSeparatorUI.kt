package com.djambulat69.fragmentchat.ui.chat

import android.view.View
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class DateSeparatorUI(
    val date: String,
) : ViewTyped {
    override val viewType: Int = R.layout.date_separator
    override val id: String = date
    override val click: View.OnLongClickListener? = null
}
