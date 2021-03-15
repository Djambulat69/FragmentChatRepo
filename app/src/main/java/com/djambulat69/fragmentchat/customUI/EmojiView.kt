package com.djambulat69.fragmentchat.customUI

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.spToPx

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttrs: Int = 0,
    @StyleRes defStyleRes: Int = R.style.Widget_FragmentChat_EmojiView
) : View(
    context, attrs, defStyleAttrs, defStyleRes
) {

    var reactionCount: Int = 5
        set(value) {
            if (value != reactionCount) {
                field = value
                requestLayout()
            }
        }
    private var contentSize: Float
    private var emoji = String(Character.toChars(0x1F600))

    fun setEmoji(code: Int) {
        emoji = String(Character.toChars(code))
        invalidate()
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.EmojiView, defStyleAttrs, defStyleRes)
            .run {
                reactionCount = getInt(R.styleable.EmojiView_reaction_count, DEF_REACTION_COUNT)
                contentSize = getDimension(
                    R.styleable.EmojiView_content_size,
                    context.spToPx(DEF_CONTENT_SIZE).toFloat()
                )
                recycle()
            }
    }

    private var viewRect = RectF()
    private var contentWidth = 0
    private var contentHeight = 0
    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = contentSize
        color = Color.WHITE
    }
    private var emojiBounds = Rect()
    private var reactionCountBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        textPaint.getTextBounds(emoji, 0, emoji.length, emojiBounds)
        textPaint.getTextBounds(
            reactionCount.toString(),
            0,
            reactionCount.toString().length,
            reactionCountBounds
        )
        contentWidth =
            resolveSize(
                emojiBounds.width() + reactionCountBounds.width() + paddingStart + paddingEnd,
                widthMeasureSpec
            )
        contentHeight =
            resolveSize(
                emojiBounds.height() + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        viewRect.right = contentWidth.toFloat()
        viewRect.bottom = contentHeight.toFloat()
        setMeasuredDimension(contentWidth, contentHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(
            emoji,
            paddingStart.toFloat(),
            (contentHeight / 2f + emojiBounds.height() / 4f),
            textPaint
        )
        canvas?.drawText(
            reactionCount.toString(),
            (contentWidth - reactionCountBounds.width() - paddingEnd).toFloat(),
            (contentHeight / 2f + reactionCountBounds.height() / 2f),
            textPaint
        )
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isSelected) {
            mergeDrawableStates(drawableState, DRAWABLES_STATE)
        }
        return drawableState
    }

    override fun performClick(): Boolean {
        isSelected = !isSelected
        return super.performClick()
    }


    companion object {
        private const val DEF_REACTION_COUNT = 5
        private const val DEF_CONTENT_SIZE = 35
        private val DRAWABLES_STATE = IntArray(1) { android.R.attr.state_selected }
    }
}
