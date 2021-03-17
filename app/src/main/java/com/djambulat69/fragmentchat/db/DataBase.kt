package com.djambulat69.fragmentchat.db

import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*


object DataBase {
    private var _messages = listOf(
        Message(
            UUID.randomUUID().toString(), "Message text", "Author Author", mutableListOf(
                Reaction(0x1F600, 5, false)
            ),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(2000000000))
        )
    )
    val messages: BehaviorSubject<List<Message>> = BehaviorSubject.create()

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

    fun updateReactionInMessage(message: Message, reactions: MutableList<Reaction>) {
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
