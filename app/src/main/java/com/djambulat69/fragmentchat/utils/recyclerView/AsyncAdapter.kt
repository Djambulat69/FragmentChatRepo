package com.djambulat69.fragmentchat.utils.recyclerView

import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import io.reactivex.rxjava3.core.Observable

class AsyncAdapter<VT : ViewTyped>(
    holderFactory: HolderFactory<VT>,
    diffCallback: DiffUtil.ItemCallback<VT>,
    private val clickMapper: ClickMapper<ClickTypes>? = null
) : BaseAdapter<VT>(holderFactory) {

    private val differ = AsyncListDiffer(this, diffCallback)
    override var items: List<VT>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    fun <CT : ClickTypes> getClicks(): Observable<CT> =
        holderFactory.getClicksObservable().map {
            clickMapper?.map(it, items) as CT? ?: throw IllegalStateException("To get clicks clickmapper must not be null")
        }
}
