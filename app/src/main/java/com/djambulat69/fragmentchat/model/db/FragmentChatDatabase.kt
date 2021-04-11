package com.djambulat69.fragmentchat.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.Stream

@Database(entities = [Stream::class, Message::class], version = 1, exportSchema = false)
@TypeConverters(com.djambulat69.fragmentchat.model.db.TypeConverters::class)
abstract class FragmentChatDatabase : RoomDatabase() {

    abstract fun streamsDao(): StreamsDao
    abstract fun messagesDao(): MessagesDao

    companion object {

        private const val DATABASE_NAME = "fragmentChatDatabase"

        @Volatile
        private var INSTANCE: FragmentChatDatabase? = null

        fun get(context: Context): FragmentChatDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FragmentChatDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
