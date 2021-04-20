package com.djambulat69.fragmentchat.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.emoji.bundled.BundledEmojiCompatConfig
import androidx.emoji.text.EmojiCompat
import com.djambulat69.fragmentchat.model.MyUser
import com.djambulat69.fragmentchat.model.db.FragmentChatDatabase
import com.djambulat69.fragmentchat.model.network.GetEventsResponse
import com.djambulat69.fragmentchat.model.network.ZulipServiceImpl
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

private const val TAG = "FragmentChatApplication"

class FragmentChatApplication : Application() {

    private val messagesDao by lazy { FragmentChatDatabase.INSTANCE.messagesDao() }
    private val compositeDisposable = CompositeDisposable()
    private val eventsSubject = PublishSubject.create<GetEventsResponse>()

    init {
        INSTANCE = this
    }

    override fun onCreate() {
        super.onCreate()

        EmojiCompat.init(
            BundledEmojiCompatConfig(applicationContext).setReplaceAll(true)
        )
        // Пока просто наброски
        val myUserId = MyUser.getId()

        compositeDisposable.add(
            eventsSubject
                .observeOn(Schedulers.io())
                .subscribe(
                    { getResponse ->
                        Log.d(TAG, getResponse.toString())
                        getResponse.events.forEach { event ->
                            event.message?.takeIf { it.senderId != myUserId }?.let { messagesDao.saveMessageSync(it) }
                        }
                        ZulipServiceImpl.getEventQueue(getResponse.queueId!!, getResponse.events.last().id)
                            .subscribe(
                                { getResponse2 ->
                                    eventsSubject.onNext(getResponse2)
                                },
                                { logError(it) }
                            )
                    },
                    { logError(it) }
                )
        )

        compositeDisposable.add(
            ZulipServiceImpl.registerEventQueue()
                .subscribeOn(Schedulers.io())
                .flatMap { registerResponse ->
                    Log.d(TAG, registerResponse.toString())
                    ZulipServiceImpl.getEventQueue(registerResponse.queueId, registerResponse.lastEventId)
                }
                .subscribe(
                    { eventsSubject.onNext(it) },
                    { logError(it) }
                )
        )
    }

    private fun logError(e: Throwable) {
        Log.d(TAG, e.stackTraceToString())
    }

    companion object {

        lateinit var INSTANCE: FragmentChatApplication

        fun applicationContext(): Context = INSTANCE.applicationContext
    }
}
