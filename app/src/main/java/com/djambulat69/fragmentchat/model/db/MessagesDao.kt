package com.djambulat69.fragmentchat.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.djambulat69.fragmentchat.model.network.Message
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface MessagesDao {

    @Query("SELECT * FROM messages_table WHERE topicName = :topicName AND streamId = :streamId")
    fun getMesssages(topicName: String, streamId: Int): Flowable<List<Message>>

    @Insert
    fun saveMessages(messages: List<Message>): Completable

}
