package com.djambulat69.fragmentchat.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.djambulat69.fragmentchat.R
import com.google.android.material.imageview.ShapeableImageView

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttrs, defStyleRes) {

    private val avatarView: ShapeableImageView
    private var avatarSize = 0

    private val messageLayout: LinearLayout
    private var messageWidth = 0
    private var messageHeight = 0

    private val flexBox: FlexBoxLayout
    private var flexBoxWidth = 0
    private var flexBoxHeight = 0

    init {
        LayoutInflater.from(context).inflate(R.layout.message_viewgroup_layout, this, true)
        avatarView = findViewById(R.id.avatar_view)
        messageLayout = findViewById(R.id.message_linear_layout)
        flexBox = findViewById(R.id.flex_box)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        avatarSize =
            maxOf(
                avatarView.measuredWidth + avatarView.marginStart + avatarView.marginEnd,
                avatarView.measuredHeight + avatarView.marginTop + avatarView.marginBottom
            )
        messageWidth =
            messageLayout.measuredWidth + messageLayout.marginStart + messageLayout.marginEnd
        messageHeight =
            messageLayout.measuredHeight + messageLayout.marginTop + messageLayout.marginBottom
        flexBoxWidth = flexBox.measuredWidth + flexBox.marginStart + flexBox.marginEnd
        flexBoxHeight = flexBox.measuredHeight + flexBox.marginTop + flexBox.marginBottom
        setMeasuredDimension(
            resolveSize(
                avatarSize + maxOf(messageWidth, flexBoxWidth) + paddingStart + paddingEnd,
                widthMeasureSpec
            ),
            resolveSize(
                maxOf(avatarSize, messageHeight + flexBoxHeight) + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        avatarView.layout(
            avatarView.marginStart + paddingStart,
            avatarView.marginTop + paddingTop,
            paddingStart + avatarView.marginStart + avatarView.measuredWidth,
            paddingTop + avatarView.marginTop + avatarView.measuredHeight
        )
        messageLayout.layout(
            paddingStart + avatarSize + messageLayout.marginStart,
            messageLayout.marginTop + paddingTop,
            paddingStart + avatarSize + messageLayout.marginStart + messageLayout.measuredWidth,
            paddingTop + messageLayout.marginTop + messageLayout.measuredHeight
        )
        flexBox.layout(
            paddingStart + avatarSize + flexBox.marginStart,
            paddingTop + messageHeight + flexBox.marginTop,
            paddingStart + avatarSize + flexBox.marginStart + flexBox.measuredWidth,
            paddingTop + messageHeight + flexBox.marginTop + flexBox.measuredHeight
        )
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(params: LayoutParams?): LayoutParams =
        MarginLayoutParams(params)
}
