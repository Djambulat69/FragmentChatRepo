package com.djambulat69.fragmentchat.di

import com.djambulat69.fragmentchat.ui.MainActivity
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.channels.streams.StreamsFragment
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.stream.StreamChatFragment
import com.djambulat69.fragmentchat.ui.chat.topic.TopicChatFragment
import com.djambulat69.fragmentchat.ui.people.PeopleFragment
import com.djambulat69.fragmentchat.ui.profile.ProfileFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [DatabaseModule::class, NetworkModule::class, UriReaderModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
    fun inject(channelsFragment: ChannelsFragment)
    fun inject(streamsFragment: StreamsFragment)
    fun inject(topicChatFragment: TopicChatFragment)
    fun inject(streamChatFragment: StreamChatFragment)
    fun inject(peopleFragment: PeopleFragment)
    fun inject(profileFragment: ProfileFragment)
    fun inject(emojiBottomSheetDialog: EmojiBottomSheetDialog)

}
