package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.ViewGroup

abstract class HolderFactory : (ViewGroup, Int) -> BaseViewHolder<ViewTyped>() {

    abstract fun createHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped>

    override fun invoke(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ViewTyped> =
        createHolder(viewGroup, viewType)
}
