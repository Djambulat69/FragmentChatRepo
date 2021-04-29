package com.djambulat69.fragmentchat.customUI

import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.withTranslation
import androidx.emoji.text.EmojiCompat
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.spToPx
import com.djambulat69.fragmentchat.utils.toEmoji

private const val EMOJI_LAYOUT_WIDTH = 80

class EmojiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttrs: Int = 0,
    @StyleRes defStyleRes: Int = R.style.Widget_FragmentChat_EmojiView
) : View(
    context, attrs, defStyleAttrs, defStyleRes
) {

    var emojiName: String = "no name"
    var reactionCount: Int = 5
        set(value) {
            if (value != reactionCount) {
                field = value
                requestLayout()
            }
        }

    private var contentSize: Float
    private var emoji: CharSequence = 0x1F600.toEmoji()

    fun setEmoji(code: String) {
        val processed = EmojiCompat.get().process(code)
        emoji = processed
        emojiLayout = StaticLayout(emoji, textPaint, EMOJI_LAYOUT_WIDTH, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
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
    private var textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = contentSize
        color = Color.WHITE
    }
    private var emojiLayout =
        StaticLayout(emoji, textPaint, EMOJI_LAYOUT_WIDTH, Layout.Alignment.ALIGN_CENTER, 1f, 0f, false)
    private var reactionCountBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        textPaint.getTextBounds(
            reactionCount.toString(),
            0,
            reactionCount.toString().length,
            reactionCountBounds
        )
        contentWidth =
            resolveSize(
                emojiLayout.width + reactionCountBounds.width() + paddingStart + paddingEnd,
                widthMeasureSpec
            )
        contentHeight =
            resolveSize(
                emojiLayout.height + paddingTop + paddingBottom,
                heightMeasureSpec
            )
        viewRect.right = contentWidth.toFloat()
        viewRect.bottom = contentHeight.toFloat()
        setMeasuredDimension(contentWidth, contentHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.withTranslation(
            paddingStart / 2f,
            (paddingTop.toFloat() + paddingBottom.toFloat()) / 1.5f
        ) {
            emojiLayout.draw(this)
        }
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
