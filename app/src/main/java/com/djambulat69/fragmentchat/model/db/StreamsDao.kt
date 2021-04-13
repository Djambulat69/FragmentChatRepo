package com.djambulat69.fragmentchat.model.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.djambulat69.fragmentchat.model.network.Stream
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface StreamsDao {

    @Query("SELECT * FROM streams_table WHERE NOT isSubscribed ORDER BY streamId")
    fun getStreams(): Flowable<List<Stream>>

    @Query("SELECT * FROM streams_table WHERE isSubscribed ORDER BY streamId")
    fun getSubscribedStreams(): Flowable<List<Stream>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveStreams(streams: List<Stream>): Completable

}
