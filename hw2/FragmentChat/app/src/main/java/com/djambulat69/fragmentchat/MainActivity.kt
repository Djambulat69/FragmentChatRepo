package com.djambulat69.fragmentchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.djambulat69.fragmentchat.customUI.EmojiView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        emojiViewIds.forEach{ id ->
            findViewById<EmojiView>(id).setOnClickListener {  }
        }
    }
}
