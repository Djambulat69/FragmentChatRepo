package com.djambulat69.fragmentchat.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.djambulat69.fragmentchat.model.db.converters.ReactionsConverter
import com.djambulat69.fragmentchat.model.db.converters.TopicsConverter
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.Stream

@Database(entities = [Stream::class, Message::class], version = 1, exportSchema = false)
@TypeConverters(TopicsConverter::class, ReactionsConverter::class)
abstract class FragmentChatDatabase : RoomDatabase() {

    abstract fun streamsDao(): StreamsDao
    abstract fun messagesDao(): MessagesDao

    companion object {
        const val DATABASE_NAME = "fragmentChatDatabase"
    }
}
