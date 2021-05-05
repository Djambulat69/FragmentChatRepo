package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private val context: Context,
    private val items: List<ViewTyped>,
) : RecyclerView.ItemDecoration() {

    private val layoutInflater = LayoutInflater.from(context)

    private val msgItemsIndexed = items
        .mapIndexed { i, viewTyped ->
            i to viewTyped
        }
        .filter { pair ->
            pair.second is MessageUI
        }
        .map { pair ->
            pair.first to (pair.second as MessageUI).message
        }

    private val headerTopicNames =
        msgItemsIndexed
            .filterIndexed { index, pair ->
                if (index == 0) true
                else {
                    msgItemsIndexed[index - 1].second.topicName != pair.second.topicName
                }
            }
            .map { pair ->
                val topicView = (layoutInflater.inflate(R.layout.topic_title_view, root, false) as TextView)
                    .apply { text = context.resources.getString(R.string.topic_title, pair.second.topicName) }
                pair.first to topicView
            }
            .toMap()

    private val rect = Rect(0, 0, 300, 50)
    private val paint = Paint().apply { color = Color.YELLOW }
    private val textPaint = Paint().apply { color = Color.BLACK; textSize = 30f }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        var earliestFoundHeaderPosition = -1
        var prevHeaderTop = Int.MAX_VALUE

        for (i in parent.childCount - 1 downTo 0) {
            val view = parent.getChildAt(i) ?: continue

            val viewTop = view.top - view.marginTop + view.translationY.toInt()
            if (view.bottom > 0 && viewTop - context.resources.getDimension(R.dimen.chat_topic_title_height)
                    .roundToInt() < parent.height
            ) {
                val position = parent.getChildAdapterPosition(view)
                headerTopicNames[position]?.let { topicView ->
                    val top =
                        (viewTop - context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt()).coerceAtLeast(0)
                            .coerceAtMost(
                                prevHeaderTop - context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt()
                            )
                    c.withTranslation(y = top.toFloat()) {
//                        drawRect(rect, paint)
                        topicView.measure(
                            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(
                                context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt(),
                                View.MeasureSpec.EXACTLY
                            )
                        )
                        topicView.layout(0, top, topicView.measuredWidth, top + topicView.measuredHeight)
                        topicView.draw(c)
//                        drawText(topicName, 0f, 50f, textPaint)
                    }
                    earliestFoundHeaderPosition = position
                    prevHeaderTop = viewTop - context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt()
                }
            }
        }

        if (earliestFoundHeaderPosition < 0) {
            earliestFoundHeaderPosition = parent.getChildAdapterPosition(parent[0]) + 1
        }

        for (headerPos in headerTopicNames.keys.reversed()) {
            if (headerPos < earliestFoundHeaderPosition) {
                headerTopicNames[headerPos]?.let { topicView ->
                    val top = (prevHeaderTop - context.resources.getDimension(R.dimen.chat_topic_title_height)
                        .roundToInt()).coerceAtMost(0)
                    c.withTranslation(y = top.toFloat()) {
//                        drawRect(rect, paint)
//                        drawText(topicName, 0f, 50f, textPaint)
                        topicView.measure(
                            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(
                                context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt(),
                                View.MeasureSpec.EXACTLY
                            )
                        )
                        topicView.layout(0, top, topicView.measuredWidth, top + topicView.measuredHeight)
                        topicView.draw(c)
                    }
                }
                break
            }
        }
    }


    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val childPosition = parent.getChildAdapterPosition(view)
        if (childPosition in headerTopicNames.keys) {
            outRect.top = context.resources.getDimension(R.dimen.chat_topic_title_height).roundToInt()
        }
    }

}
