package com.djambulat69.fragmentchat.model.db

import com.djambulat69.fragmentchat.model.*
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*

object DataBase {
    val profile = Profile("Name Surname", "In a meeting", "OOAAA@gmail.com")

    val users = listOf<User>(
        User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
        User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
        User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
        User("Darrell Steward", "darrel@company.com", UUID.randomUUID()),
    )

    private var _messages = listOf(
        Message(
            UUID.randomUUID().toString(), "Message text", "Author Author", mutableListOf(
                Reaction(0x1F600, 5, false)
            ),
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(2000000000))
        )
    )
    val messagesSubject: BehaviorSubject<List<Message>> = BehaviorSubject.create()

    val streams = listOf(
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

    init {
        messagesSubject.onNext(_messages)
    }

    fun sendMessage(msg: Message) {
        _messages = _messages + msg
        messagesSubject.onNext(_messages)
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) {
        message.reactions.add(Reaction(emojiCode, 1, true))

        _messages = _messages.map { if (it.id == message.id) message else it }
        messagesSubject.onNext(_messages)
    }

    fun updateReactionsInMessage(message: Message, reactions: MutableList<Reaction>) {
        message.reactions = reactions

        _messages = _messages.map { if (it.id == message.id) message else it }
        messagesSubject.onNext(_messages)
    }
}
