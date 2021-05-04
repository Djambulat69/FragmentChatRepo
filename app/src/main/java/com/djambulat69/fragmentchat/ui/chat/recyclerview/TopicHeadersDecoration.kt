package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.withTranslation
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class TopicHeadersDecoration(
    private val context: Context,
    private val items: List<ViewTyped>,
) : RecyclerView.ItemDecoration() {


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
                pair.first to pair.second.topicName
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

            val viewTop = view.top + view.translationY.toInt()
            if (view.bottom > 0 && viewTop < parent.height) {
                val position = parent.getChildAdapterPosition(view)
                headerTopicNames[position]?.let { topicName ->
                    val top = viewTop.coerceAtLeast(0).coerceAtMost(prevHeaderTop - rect.height())
                    c.withTranslation(y = top.toFloat()) {
                        drawRect(rect, paint)
                        drawText(topicName, 0f, 50f, textPaint)
                    }
                    earliestFoundHeaderPosition = position
                    prevHeaderTop = viewTop
                }
            }
        }

        if (earliestFoundHeaderPosition < 0) {
            earliestFoundHeaderPosition = parent.getChildAdapterPosition(parent[0]) + 1
        }

        for (headerPos in headerTopicNames.keys.reversed()) {
            if (headerPos < earliestFoundHeaderPosition) {
                headerTopicNames[headerPos]?.let { topicName ->
                    val top = (prevHeaderTop - rect.height()).coerceAtMost(0)
                    c.withTranslation(y = top.toFloat()) {
                        drawRect(rect, paint)
                        drawText(topicName, 0f, 50f, textPaint)
                    }
                }
                break
            }
        }
    }

}
