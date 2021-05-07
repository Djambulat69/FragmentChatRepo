package com.djambulat69.fragmentchat.ui

import android.app.Application
import android.content.Context
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.djambulat69.fragmentchat.di.AppComponent
import com.djambulat69.fragmentchat.di.DaggerAppComponent

private const val TAG = "FragmentChatApplication"

class FragmentChatApplication : Application() {

    val daggerAppComponent: AppComponent = DaggerAppComponent.create()

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        EmojiCompat.init(
            BundledEmojiCompatConfig(applicationContext).setReplaceAll(true)
        )
    }


    companion object {

        lateinit var INSTANCE: FragmentChatApplication

        fun applicationContext(): Context = INSTANCE.applicationContext
    }
}
