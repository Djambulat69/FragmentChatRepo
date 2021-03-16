package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.View

interface ViewTyped {
    val viewType: Int
    val id: Long
    val click: View.OnClickListener?
}
