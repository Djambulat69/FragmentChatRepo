package com.djambulat69.fragmentchat.ui.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import io.reactivex.rxjava3.core.Observable

private const val MESSAGES_PREFETCH_DISTANCE = 5
private const val MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL = 2

fun getScrollObservable(recyclerView: RecyclerView): Observable<Long> = Observable.create { emitter ->
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            loadNextPage(recyclerView)
        }

        private fun loadNextPage(recyclerView: RecyclerView) {
            if (recyclerView.adapter != null) {

                val itemsRemaining = (recyclerView.layoutManager as LinearLayoutManager)
                    .findFirstVisibleItemPosition()

                if (itemsRemaining < MESSAGES_PREFETCH_DISTANCE && itemsRemaining != RecyclerView.NO_POSITION) {
                    val lastLoadedMessageId =
                        (recyclerView.adapter as AsyncAdapter<ViewTyped>).items.first { uiItem -> uiItem is MessageUI }.id.toLong()
                    emitter.onNext(lastLoadedMessageId)
                }
            }
        }
    })
}

fun messagesToMessageUIs(messages: List<Message>) = messages.map { message -> MessageUI(message) }

fun RecyclerView.Adapter<*>.registerAutoScrollAdapterDataObserver(chatRecyclerView: RecyclerView) =
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            chatRecyclerView.adapter?.let {
                if (positionStart > MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL) {
                    chatRecyclerView.scrollToPosition(it.itemCount - 1)
                }
            }
        }
    })
