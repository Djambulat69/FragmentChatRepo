package com.djambulat69.fragmentchat.model.db

import com.djambulat69.fragmentchat.model.*
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

object DataBase {

    private val profile get() = Profile("Name Surname", "In a meeting", "OOAAA@gmail.com")
    val profileSingle: Single<Profile> = Single.fromCallable {
        maybeError()
        profile
    }

    private val users
        get() = listOf(
            User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
            User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
            User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
            User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
        )
    val usersSingle: Single<List<User>> = Single.fromCallable {
        maybeError()
        users
    }

    private var messages = listOf(
        Message(
            UUID.randomUUID().toString(), "Message text", "Author Author", mutableListOf(
                Reaction(0x1F600, 5, false)
            ),
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(2000000000))
        )
    )
    val messagesSubject: PublishSubject<List<Message>> = PublishSubject.create()
    val messagesSingle: Single<List<Message>> = Single.fromCallable { messages }

    private val streams
        get() = listOf(
            Stream(
                "general",
                listOf(
                    Topic("Testing", 332),
                    Topic("Developers", 52),
                    Topic("Tinkoff", 3)
                ),
                true
            ),
            Stream(
                "Design", listOf(
                    Topic("Main", 23),
                    Topic("Prod", 92)
                ),
                true
            ),
            Stream(
                "Genders", listOf(
                    Topic("Male", 23),
                    Topic("Female", 92)
                ),
                false
            ),
            Stream(
                "PR", listOf(
                    Topic("prStrategy", 92)
                ),
                false
            )
        )
    val streamsSingle: Single<List<Stream>> = Single.fromCallable {
        maybeError()
        streams
    }

    fun sendMessage(msg: Message): Maybe<Boolean> = Maybe.create { emitter ->
        maybeError()
        messages = messages + msg
        messagesSubject.onNext(messages)
        emitter.onSuccess(true)
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) {
        message.reactions.add(Reaction(emojiCode, 1, true))

        messages = messages.map { if (it.id == message.id) message else it }
        messagesSubject.onNext(messages)
    }

    fun updateReactionsInMessage(message: Message, reactions: MutableList<Reaction>) {
        message.reactions = reactions

        messages = messages.map { if (it.id == message.id) message else it }
        messagesSubject.onNext(messages)
    }

    fun searchStreams(query: String): Single<List<Stream>> = Single.fromCallable {
        maybeError()
        streams.filter { it.title.startsWith(query, ignoreCase = true) }
    }

    private fun maybeError() {
        val num = (0..2).shuffled().first()
        if (num == 0) throw Exception("DataBase couldn't load files")
    }
}
