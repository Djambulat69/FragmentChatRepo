package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder<T : ViewTyped>(
    protected val holderView: View,
) : RecyclerView.ViewHolder(holderView) {

    open fun bind(item: T) = Unit

}
