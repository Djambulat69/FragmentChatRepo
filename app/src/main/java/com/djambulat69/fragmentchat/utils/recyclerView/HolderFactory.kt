package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.inflate

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped>() {

    abstract fun createHolder(
        parent: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewTyped>

    override fun invoke(
        viewGroup: ViewGroup,
        viewType: Int,
    ): BaseViewHolder<ViewTyped> {
        val view = viewGroup.inflate<View>(viewType)
        return when (viewType) {
            R.layout.loading_header -> BaseViewHolder(view)
            else -> createHolder(viewGroup, viewType)
        }
    }
}
