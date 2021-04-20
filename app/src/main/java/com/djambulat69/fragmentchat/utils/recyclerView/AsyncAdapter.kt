package com.djambulat69.fragmentchat.utils.recyclerView

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil

class AsyncAdapter(holderFactory: HolderFactory, diffCallback: DiffUtil.ItemCallback<ViewTyped>) : BaseAdapter(holderFactory) {

    private val differ = AsyncListDiffer(this, diffCallback)
    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value)

}
