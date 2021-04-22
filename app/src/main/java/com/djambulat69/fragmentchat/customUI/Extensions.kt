package com.djambulat69.fragmentchat.customUI

import android.view.LayoutInflater
import android.view.View
import androidx.core.view.isVisible
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.MyUser
import com.djambulat69.fragmentchat.model.network.Reaction
import com.djambulat69.fragmentchat.utils.toEmoji

private const val BITS_IN_UNICODE = 16

fun FlexBoxLayout.setReactions(
    reactions: List<Reaction>,
    reactionClick: (View) -> Unit
) {
    val myUserId = MyUser.getId()

    removeViews(0, childCount - 1)
    isVisible = reactions.isNotEmpty()

    reactions.distinctBy { it.emojiCode }.forEach { reaction ->
        addEmojiViewByReaction(
            reaction,
            reactions.count { it.emojiCode == reaction.emojiCode },
            reactionClick,
            myUserId in reactions.filter { it.emojiCode == reaction.emojiCode }.map { it.userId }
        )
    }
}

private fun FlexBoxLayout.addEmojiViewByReaction(
    reaction: Reaction,
    count: Int,
    reactionClick: (View) -> Unit,
    isSet: Boolean
) {
    addView(
        (LayoutInflater.from(context).inflate(R.layout.emoji_view_item, this, false) as EmojiView).apply {
            setEmoji(reaction.emojiCode.toInt(BITS_IN_UNICODE).toEmoji())
            reactionCount = count
            emojiName = reaction.emojiName
            setOnClickListener {
                updateEmojiViewOnClick()
                reactionClick(it)
                if (reactionCount == 0) {
                    removeView(it)
                    this@addEmojiViewByReaction.isVisible = childCount > 1
                }
            }
            isSelected = isSet
        }, childCount - 1
    )
}

private fun EmojiView.updateEmojiViewOnClick() {
    if (isSelected) {
        reactionCount += 1
    } else {
        reactionCount -= 1
    }
}
