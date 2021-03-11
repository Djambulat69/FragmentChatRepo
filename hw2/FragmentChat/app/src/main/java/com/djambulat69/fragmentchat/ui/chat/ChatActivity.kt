package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.customUI.EmojiView

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val emojiViewIds = listOf(
            R.id.emoji_view_1,
            R.id.emoji_view_2,
            R.id.emoji_view_3,
            R.id.emoji_view_4,
            R.id.emoji_view_5,
            R.id.emoji_view_6,
            R.id.emoji_view_7,
            R.id.emoji_view_8,
            R.id.emoji_view_9
        )
        emojiViewIds.forEach { id ->
            findViewById<EmojiView>(id).setOnClickListener { }
        }
    }
}
