package com.djambulat69.fragmentchat.di

import com.djambulat69.fragmentchat.ui.MainActivity
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.channels.streams.StreamsFragment
import com.djambulat69.fragmentchat.ui.chat.ChatFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(channelsFragment: ChannelsFragment)
    fun inject(streamsFragment: StreamsFragment)
    fun inject(chatFragment: ChatFragment)


}
