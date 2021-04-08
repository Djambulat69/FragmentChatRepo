package com.djambulat69.fragmentchat.ui

import android.app.Application
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat

class FragmentChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        EmojiCompat.init(
            BundledEmojiCompatConfig(applicationContext).setReplaceAll(true)
        )
    }
}
