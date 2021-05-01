package com.djambulat69.fragmentchat.model.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.djambulat69.fragmentchat.model.db.converters.ReactionsConverter
import com.djambulat69.fragmentchat.model.db.converters.TopicsConverter
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.ui.FragmentChatApplication

@Database(entities = [Stream::class, Message::class], version = 1, exportSchema = false)
@TypeConverters(TopicsConverter::class, ReactionsConverter::class)
abstract class FragmentChatDatabase : RoomDatabase() {

    abstract fun streamsDao(): StreamsDao
    abstract fun messagesDao(): MessagesDao

    companion object {

        private const val DATABASE_NAME = "fragmentChatDatabase"

        val INSTANCE: FragmentChatDatabase by lazy {
            return@lazy Room.databaseBuilder(
                FragmentChatApplication.applicationContext(),
                FragmentChatDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }
}
