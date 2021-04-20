package com.djambulat69.fragmentchat.customUI

import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.MyUser
import com.djambulat69.fragmentchat.model.network.Reaction
import com.djambulat69.fragmentchat.utils.toEmoji
import kotlin.math.roundToInt

private const val BITS_IN_UNICODE = 16

fun FlexBoxLayout.setReactions(
    reactions: List<Reaction>,
    reactionClick: (Boolean, Int, String) -> Unit,
    messageId: Int
) {
    val myUserId = MyUser.getId()

    removeViews(0, childCount - 1)

    isVisible = reactions.isNotEmpty()

    reactions.distinctBy { it.emojiCode }.forEach { reaction ->
        addEmojiViewByReaction(
            reaction,
            reactions.count { it.emojiCode == reaction.emojiCode },
            reactionClick,
            myUserId in reactions.filter { it.emojiCode == reaction.emojiCode }.map { it.userId },
            messageId
        )
    }
}

private fun FlexBoxLayout.addEmojiViewByReaction(
    reaction: Reaction,
    count: Int,
    reactionClick: (Boolean, Int, String) -> Unit,
    isSet: Boolean,
    messageId: Int
) {
    addView(EmojiView(context).apply {
        @Px val height = resources.getDimension(R.dimen.emoji_view_height).roundToInt()
        @Px val margin = resources.getDimension(R.dimen.margin_small).roundToInt()
        @Px val heightPadding = resources.getDimension(R.dimen.padding_small).roundToInt()
        @Px val widthPadding = resources.getDimension(R.dimen.padding_medium).roundToInt()
        layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            height
        ).apply { setMargins(margin) }
        setPadding(widthPadding, heightPadding, widthPadding, heightPadding)
        setEmoji(reaction.emojiCode.toInt(BITS_IN_UNICODE).toEmoji())
        reactionCount = count
        setOnClickListener {
            updateEmojiViewOnClick()
            reactionClick(isSelected, messageId, reaction.emojiName)
            if (reactionCount == 0) {
                removeView(it)
                this@addEmojiViewByReaction.isVisible = childCount > 1
            }
        }
        isSelected = isSet
    }, childCount - 1)
}

private fun EmojiView.updateEmojiViewOnClick() {
    if (isSelected) {
        reactionCount += 1
    } else {
        reactionCount -= 1
    }
}
