package com.djambulat69.fragmentchat.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djambulat69.fragmentchat.model.network.Message
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE topicName = :topicName AND streamId = :streamId")
    fun getTopicMessages(topicName: String, streamId: Int): Flowable<List<Message>>

    @Query("SELECT * FROM messages_table WHERE streamId = :streamId")
    fun getStreamMessages(streamId: Int): Flowable<List<Message>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMessages(messages: List<Message>): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMessageSync(message: Message)

    @Query("DELETE FROM messages_table WHERE topicName = :topicName AND streamId = :streamId")
    fun deleteTopicMessages(topicName: String, streamId: Int): Completable

    @Query("DELETE FROM messages_table WHERE streamId = :streamId")
    fun deleteStreamMessages(streamId: Int): Completable

}
