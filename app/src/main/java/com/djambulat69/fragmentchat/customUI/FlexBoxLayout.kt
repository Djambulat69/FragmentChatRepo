package com.djambulat69.fragmentchat.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.*

class FlexBoxLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    init {
//        setWillNotDraw(true)
    }

    private var layoutWidth = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingEnd - paddingStart
        var heightUsed = 0
        var widthUsed = 0
        var maxHeightInLine = 0
        var maxWidthUsed = 0
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        var isOneLine = true
        children.forEach { child ->
            val childWidth = if (child.isGone) 0 else child.measuredWidth + child.marginStart + child.marginEnd
            val childHeight = if (child.isGone) 0 else child.measuredHeight + child.marginTop + child.marginBottom
            if (widthUsed + childWidth > layoutWidth) {
                maxWidthUsed = maxOf(widthUsed, maxWidthUsed)
                widthUsed = 0
                heightUsed += maxHeightInLine
                maxHeightInLine = 0
                isOneLine = false
            }
            maxHeightInLine =
                maxOf(maxHeightInLine, childHeight)
            widthUsed +=
                childWidth
        }
        if (isOneLine)
            maxWidthUsed = widthUsed

        setMeasuredDimension(
            resolveSize(maxWidthUsed + paddingStart + paddingEnd, widthMeasureSpec),
            resolveSize(
                maxHeightInLine + heightUsed + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var heightUsed = 0
        var widthUsed = 0
        var maxHeightInLine = 0
        children.forEach { child ->
            val childWidth = child.measuredWidth + child.marginStart + child.marginEnd
            val childHeight = child.measuredHeight + child.marginTop + child.marginBottom
            maxHeightInLine =
                maxOf(maxHeightInLine, childHeight)
            if (widthUsed + childWidth > layoutWidth) {
                widthUsed = 0
                heightUsed += maxHeightInLine
                maxHeightInLine = 0
            }
            child.layout(
                widthUsed + child.marginStart + paddingStart,
                heightUsed + child.marginTop + paddingTop,
                widthUsed + child.measuredWidth + child.marginStart + paddingStart,
                heightUsed + child.measuredHeight + child.marginTop + paddingTop
            )
            widthUsed += childWidth
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(params: LayoutParams?): LayoutParams =
        MarginLayoutParams(params)
}
