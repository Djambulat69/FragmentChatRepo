package com.djambulat69.fragmentchat.ui.chat

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.chat.recyclerview.DateSeparatorUI
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import com.djambulat69.fragmentchat.ui.chat.recyclerview.TopicTitleUI
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.secondsToDateString
import io.reactivex.rxjava3.core.Observable

const val NO_TOPIC_TITLE = "(no topic)"

private const val MESSAGES_PREFETCH_DISTANCE = 5
private const val MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL = 3

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

fun messagesByDate(messages: List<Message>, diffTopics: Boolean): List<ViewTyped> =
    messages.groupBy { secondsToDateString(it.timestamp.toLong()) }
        .flatMap { (date: String, messagesByDate: List<Message>) ->
            listOf(DateSeparatorUI(date)) +
                    if (diffTopics) {
                        messagesToMessageUIsDiffTopics(messagesByDate)
                    } else {
                        messagesToMessageUIsSameTopic(messages)
                    }
        }


fun RecyclerView.Adapter<*>.registerAutoScrollAdapterDataObserver(chatRecyclerView: RecyclerView) =
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)

            chatRecyclerView.adapter?.let {
                if (positionStart > it.itemCount - MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL) {
                    chatRecyclerView.scrollToPosition(it.itemCount - 1)
                }
            }
        }
    })

private fun messagesToMessageUIsDiffTopics(messages: List<Message>): List<ViewTyped> {

    return messages.flatMapIndexed { i: Int, message: Message ->
        if (i == 0 || messages[i - 1].topicName != message.topicName) {
            listOf(TopicTitleUI(message.topicName), MessageUI(message))
        } else {
            listOf(MessageUI(message))
        }
    }

}

private fun messagesToMessageUIsSameTopic(messages: List<Message>): List<ViewTyped> =
    messages.map { MessageUI(it) }
