package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.db.StreamsDao
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.model.network.TopicsResponse
import com.djambulat69.fragmentchat.model.network.ZulipRemote
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class StreamsRepository(private val streamsDao: StreamsDao) {

    private val zulipService = ZulipRemote

    fun getAllStreamsFromNetwork(): Single<StreamsResponseSealed.AllStreamsResponse> = zulipService.getStreamsSingle()

    fun getSubscribedStreamsFromNetwork(): Single<StreamsResponseSealed.SubscribedStreamsResponse> =
        zulipService.getSubscriptionsSingle()

    fun getSubscribedStreamsFromDb(): Flowable<List<Stream>> = streamsDao.getSubscribedStreams()

    fun getAllStreamsFromDb(): Flowable<List<Stream>> = streamsDao.getStreams()

    fun getTopicsFromNetwork(streamId: Int): Single<TopicsResponse> = zulipService.getTopicsSingle(streamId)

    fun saveStreams(streams: List<Stream>): Completable = streamsDao.saveStreams(streams)

}
