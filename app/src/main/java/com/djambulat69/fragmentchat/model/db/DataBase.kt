package com.djambulat69.fragmentchat.model.db

import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.Topic
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*

object DataBase {
    private var _messages = listOf(
        Message(
            UUID.randomUUID().toString(), "Message text", "Author Author", mutableListOf(
                Reaction(0x1F600, 5, false)
            ),
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(2000000000))
        )
    )
    val messages: BehaviorSubject<List<Message>> = BehaviorSubject.create()

    val streams = listOf(
        Stream("general", listOf(Topic("Testing", 332))),
        Stream("memes", listOf(Topic("Hello", 23)))
    )

    init {
        messages.onNext(_messages)
    }

    fun sendMessage(msg: Message) {
        _messages = (listOf(msg) + _messages)
        messages.onNext(_messages)
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) {
        message.reactions.add(Reaction(emojiCode, 1, true))

        _messages = _messages.map {
            if (it.id == message.id)
                message
            else
                it
        }
        messages.onNext(_messages)
    }

    fun updateReactionsInMessage(message: Message, reactions: MutableList<Reaction>) {
        message.reactions = reactions

        _messages = _messages.map {
            if (it.id == message.id)
                message
            else
                it
        }
        messages.onNext(_messages)
    }
}
