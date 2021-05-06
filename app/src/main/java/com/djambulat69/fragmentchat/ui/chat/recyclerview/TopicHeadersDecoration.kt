package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.withTranslation
import androidx.core.view.get
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import kotlin.math.roundToInt

class TopicHeadersDecoration(
    private val root: ViewGroup,
    _items: List<ViewTyped>,
) : RecyclerView.ItemDecoration() {

    var items = _items
        set(newItems) {
            field = newItems
            headerTopicViewsIndexed = getHeaderTopicViews()
        }

    private val context = root.context
    private val layoutInflater = LayoutInflater.from(context)

    private var headerTopicViewsIndexed: Map<Int, TextView> = getHeaderTopicViews()
    private val headerHeight = context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        var earliestFoundHeaderPosition = -1
        var prevHeaderTop = Int.MAX_VALUE

        for (i in parent.childCount - 1 downTo 0) {
            val view = parent.getChildAt(i) ?: continue

            val viewTop = view.top - view.marginTop + view.translationY.toInt()
            if (view.bottom > 0 && viewTop - headerHeight < parent.height
            ) {
                val position = parent.getChildAdapterPosition(view)
                headerTopicViewsIndexed[position]?.let { topicView ->
                    val top = (viewTop - headerHeight)
                        .coerceAtLeast(0)
                        .coerceAtMost(prevHeaderTop - headerHeight)

                    measureAndLayoutTopicView(topicView, top, parent)
                    c.drawHeader(topicView, top.toFloat())

                    earliestFoundHeaderPosition = position
                    prevHeaderTop = viewTop - headerHeight
                }
            }
        }

        if (earliestFoundHeaderPosition < 0) {
            earliestFoundHeaderPosition = parent.getChildAdapterPosition(parent[0]) + 1
        }

        for (headerPos in headerTopicViewsIndexed.keys.reversed()) {
            if (headerPos < earliestFoundHeaderPosition) {
                headerTopicViewsIndexed[headerPos]?.let { topicView ->
                    val top = (prevHeaderTop - headerHeight).coerceAtMost(0)
                    measureAndLayoutTopicView(topicView, top, parent)
                    c.drawHeader(topicView, top.toFloat())
                }
                break
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val childPosition = parent.getChildAdapterPosition(view)
        if (childPosition in headerTopicViewsIndexed.keys) {
            outRect.top = headerHeight
        }
    }

    private fun measureAndLayoutTopicView(topicView: TextView, top: Int, parent: RecyclerView) {

        topicView.measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(headerHeight, View.MeasureSpec.EXACTLY)
        )

        topicView.layout(0, top, topicView.measuredWidth, top + topicView.measuredHeight)
    }

    private fun Canvas.drawHeader(topicView: TextView, translationY: Float) {
        withTranslation(y = translationY) {
            topicView.draw(this)
        }
    }

    private fun getHeaderTopicViews(): Map<Int, TextView> {
        val topicsIndexed = items
            .withIndex()
            .filter { pair ->
                pair.value is MessageUI
            }
            .map { pair ->
                pair.index to (pair.value as MessageUI).message.topicName
            }

        return topicsIndexed
            .filterIndexed { i, pair ->
                if (i == 0) true
                else {
                    topicsIndexed[i - 1].second != pair.second
                }
            }
            .map { pair ->
                val topicView = (layoutInflater.inflate(R.layout.topic_title_view, root, false) as TextView)
                    .apply {
                        text = context.resources.getString(R.string.topic_title, pair.second)
                    }
                pair.first to topicView
            }
            .toMap()
    }

}
