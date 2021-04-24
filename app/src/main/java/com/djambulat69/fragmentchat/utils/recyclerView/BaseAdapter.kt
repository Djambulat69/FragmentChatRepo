package com.djambulat69.fragmentchat.utils.recyclerView

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<VT : ViewTyped>(protected val holderFactory: HolderFactory<VT>) :
    RecyclerView.Adapter<BaseViewHolder<VT>>() {

    abstract var items: List<VT>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VT> =
        holderFactory(parent, viewType)

    override fun onBindViewHolder(holder: BaseViewHolder<VT>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int = items[position].viewType
}
