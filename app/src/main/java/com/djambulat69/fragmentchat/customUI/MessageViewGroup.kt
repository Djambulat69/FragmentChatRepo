package com.djambulat69.fragmentchat.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.*
import com.bumptech.glide.RequestManager
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Reaction
import com.google.android.material.imageview.ShapeableImageView

class MessageViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttrs, defStyleRes) {

    private val avatarView: ShapeableImageView
    private var avatarSize = 0

    private val messageLayout: LinearLayout
    private var messageWidth = 0
    private var messageHeight = 0

    private val messageText: TextView
    private val authorText: TextView

    private val flexBox: FlexBoxLayout
    private var flexBoxWidth = 0
    private var flexBoxHeight = 0

    private val addReactionButton: View


    init {
        LayoutInflater.from(context).inflate(R.layout.message_viewgroup_layout, this, true)
        avatarView = findViewById(R.id.avatar_view)
        messageLayout = findViewById(R.id.message_linear_layout)
        authorText = findViewById(R.id.profile_name_text_view)
        messageText = findViewById(R.id.message_text_view)
        flexBox = findViewById(R.id.flex_box)
        addReactionButton = findViewById(R.id.add_reaction_button_outcoming)

        context.obtainStyledAttributes(
            attrs,
            R.styleable.MessageViewGroup,
            defStyleAttrs,
            defStyleRes
        ).run {
            text = getString(R.styleable.MessageViewGroup_text) ?: ""
            author = getString(R.styleable.MessageViewGroup_author) ?: ""
            recycle()
        }
    }

    var text
        get() = messageText.text.toString()
        set(value) {
            messageText.text = value
            requestLayout()
        }
    var author
        get() = authorText.text.toString()
        set(value) {
            authorText.text = value
            requestLayout()
        }

    fun setReactions(
        reactions: List<Reaction>,
        reactionClick: (Boolean, Int, String) -> Unit,
        messageId: Int
    ) {
        flexBox.setReactions(reactions, reactionClick, messageId)
    }

    fun setOnMessageClickListener(clickListener: () -> Unit) = messageLayout.setOnLongClickListener { clickListener(); true }

    fun setAddReactionListener(click: () -> Unit) {
        addReactionButton.setOnClickListener { click() }
    }

    fun setAvatar(glide: RequestManager, avatarUrl: String) {
        glide.load(avatarUrl).into(avatarView)
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

        if (avatarView.isGone) avatarSize = 0
        if (messageLayout.isGone) {
            messageHeight = 0
            messageWidth = 0
        }
        if (flexBox.isGone) {
            flexBoxHeight = 0
            flexBoxWidth = 0
        }
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
            paddingStart + avatarSize + maxOf(0, messageWidth - messageLayout.marginEnd),
            paddingTop + maxOf(0, messageHeight - messageLayout.marginBottom)
        )
        flexBox.layout(
            paddingStart + avatarSize + flexBox.marginStart,
            paddingTop + messageHeight + flexBox.marginTop,
            paddingStart + avatarSize + maxOf(0, flexBoxWidth - flexBox.marginEnd),
            paddingTop + messageHeight + maxOf(0, flexBoxHeight - flexBox.marginBottom)
        )
    }

    override fun generateDefaultLayoutParams(): LayoutParams =
        MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(params: LayoutParams?): LayoutParams =
        MarginLayoutParams(params)
}
