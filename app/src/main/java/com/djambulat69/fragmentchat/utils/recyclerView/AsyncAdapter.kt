package com.djambulat69.fragmentchat.utils.recyclerView

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil

class AsyncAdapter<VT : ViewTyped>(
    holderFactory: HolderFactory<VT>,
    diffCallback: DiffUtil.ItemCallback<VT>,
    clickMapper: ClickMapper<ClickTypes>? = null
) : BaseAdapter<VT>(holderFactory, clickMapper) {

    private val differ = AsyncListDiffer(this, diffCallback)

    override var items: List<VT>
        get() = differ.currentList
        set(value) = differ.submitList(value)

}
