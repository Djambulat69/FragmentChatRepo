package com.djambulat69.fragmentchat.ui

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.djambulat69.fragmentchat.model.db.FragmentChatDatabase

class FragmentChatApplication : Application() {

    val database by lazy { FragmentChatDatabase.get(this) }

    override fun onCreate() {
        super.onCreate()

        EmojiCompat.init(
            BundledEmojiCompatConfig(applicationContext).setReplaceAll(true)
        )
    }
}
