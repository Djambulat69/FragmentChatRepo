package com.djambulat69.fragmentchat.ui.chat.recyclerview

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.utils.dpToPx
import com.djambulat69.fragmentchat.utils.toEmoji

class EmojiBottomRecyclerAdapter(private val emojis: List<Int>, private val click: (Int) -> Unit) :
    RecyclerView.Adapter<EmojiBottomRecyclerAdapter.EmojiViewHolder>() {
    inner class EmojiViewHolder(private val emoji: TextView) : RecyclerView.ViewHolder(emoji) {

        init {
            emoji.setOnClickListener {
                click(emojis[adapterPosition])
            }
        }

        fun bind(code: Int) {
            emoji.text = code.toEmoji()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder =
        EmojiViewHolder(TextView(parent.context).apply {
            textSize = context.dpToPx(12).toFloat()
        })

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }

    override fun getItemCount(): Int = emojis.size
}
