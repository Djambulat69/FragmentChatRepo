package com.djambulat69.fragmentchat.di

import androidx.room.Room
import com.djambulat69.fragmentchat.model.db.FragmentChatDatabase
import com.djambulat69.fragmentchat.model.db.MessagesDao
import com.djambulat69.fragmentchat.model.db.StreamsDao
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(): FragmentChatDatabase =
        Room.databaseBuilder(
            FragmentChatApplication.applicationContext(),
            FragmentChatDatabase::class.java,
            FragmentChatDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideStreamsDao(db: FragmentChatDatabase): StreamsDao {
        return db.streamsDao()
    }

    @Singleton
    @Provides
    fun provideMessagesDao(db: FragmentChatDatabase): MessagesDao {
        return db.messagesDao()
    }

}
