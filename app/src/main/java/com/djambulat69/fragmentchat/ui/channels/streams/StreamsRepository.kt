package com.djambulat69.fragmentchat.ui.channels.streams

import com.djambulat69.fragmentchat.model.db.StreamsDao
import com.djambulat69.fragmentchat.model.network.Stream
import com.djambulat69.fragmentchat.model.network.TopicsResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class StreamsRepository(private val streamsDao: StreamsDao) {

    private val zulipService = ZulipServiceImpl

    fun updateAllStreams(): Completable =
        updateStreams(zulipService.getStreamsSingle() as Single<StreamsResponseSealed>)


    fun updateSubscribedStreams(): Completable =
        updateStreams(zulipService.getSubscriptionsSingle() as Single<StreamsResponseSealed>)

    fun getSubscribedStreams(): Flowable<List<Stream>> = streamsDao.getSubscribedStreams()

    fun getAllStreams(): Flowable<List<Stream>> = streamsDao.getStreams()

    private fun getTopics(streamId: Int): Single<TopicsResponse> = zulipService.getTopicsSingle(streamId)

    private fun updateStreams(streamsSingle: Single<StreamsResponseSealed>): Completable {
        var isSubscribed = false

        return streamsSingle
            .flattenAsObservable { streamResponse ->
                isSubscribed = streamResponse is StreamsResponseSealed.SubscribedStreamsResponse
                streamResponse.streams
            }
            .flatMapSingle { stream ->
                zipStreamWithTopics(stream, isSubscribed).subscribeOn(Schedulers.io()).retry()
            }
            .toList()
            .flatMapCompletable { streams -> streamsDao.saveStreams(streams) }
    }

    private fun zipStreamWithTopics(stream: Stream, subscribed: Boolean): Single<Stream> {
        return getTopics(stream.streamId)
            .zipWith(Single.just(stream)) { topicsResponse, _ ->
                stream.apply {
                    topics = topicsResponse.topics
                    isSubscribed = subscribed
                }
            }
    }
}
