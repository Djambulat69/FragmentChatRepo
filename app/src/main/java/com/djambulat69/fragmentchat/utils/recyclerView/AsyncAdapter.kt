package com.djambulat69.fragmentchat.utils.recyclerView

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import io.reactivex.rxjava3.core.Observable

class AsyncAdapter(
    holderFactory: HolderFactory,
    diffCallback: DiffUtil.ItemCallback<ViewTyped>,
    private val clickMapper: ClickMapper<*>? = null
) : BaseAdapter(holderFactory) {

    private val differ = AsyncListDiffer(this, diffCallback)
    override var items: List<ViewTyped>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun getClicks(): Observable<ClickTypes> =
        holderFactory.getClicksObservable().map {
            clickMapper?.map(it, items) ?: throw IllegalStateException("To get clicks clickmapper must not be null")
        }
}
