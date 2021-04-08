package com.djambulat69.fragmentchat.customUI

import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Reaction1
import com.djambulat69.fragmentchat.model.network.Reaction
import com.djambulat69.fragmentchat.utils.toEmoji
import kotlin.math.roundToInt

fun FlexBoxLayout.setReactions(
    reactions: List<Reaction>,
    updateReactions: (MutableList<Reaction1>) -> Unit
) {
    removeViews(0, childCount - 1)

    isVisible = reactions.isNotEmpty()

    reactions.distinctBy { it.emojiCode }.forEach { reaction ->
        addEmojiViewByReaction(reaction.emojiCode, reactions.count { it.emojiCode == reaction.emojiCode }, updateReactions)
    }
}

private fun FlexBoxLayout.addEmojiViewByReaction(
    emojiCode: String,
    count: Int,
    updateReactions: (MutableList<Reaction1>) -> Unit
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
        setEmoji(emojiCode.toInt(16).toEmoji())
        reactionCount = count
        setOnClickListener {
//            updateReactionOnClick(reaction)
            /*if (reaction.reactionCount == 0) {
                removeView(it)
                allReactions.remove(reaction)
                this@addEmojiViewByReaction.isVisible = allReactions.isNotEmpty()
            }
            updateReactions(allReactions)*/
        }
//        isSelected = reaction.isSet
    }, childCount - 1)
}

private fun EmojiView.updateReactionOnClick(reaction: Reaction1) {
    reaction.isSet = !reaction.isSet
    if (reaction.isSet) {
        reaction.reactionCount += 1
        reactionCount += 1
    } else {
        reaction.reactionCount -= 1
        reactionCount -= 1
    }
}
