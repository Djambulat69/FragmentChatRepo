package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.core.Observable

abstract class BaseAdapter<VT : ViewTyped>(
    protected val holderFactory: HolderFactory<VT>,
    private val clickMapper: ClickMapper<ClickTypes>? = null
) :
    RecyclerView.Adapter<BaseViewHolder<VT>>() {

    abstract var items: List<VT>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VT> =
        holderFactory(parent, viewType)

    override fun onBindViewHolder(holder: BaseViewHolder<VT>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].viewType

    fun <CT : ClickTypes> getClicks(): Observable<CT> =
        holderFactory.getClicksObservable().map {
            clickMapper?.map(it, items) as CT? ?: throw IllegalStateException("To get clicks clickmapper must not be null")
        }
}
