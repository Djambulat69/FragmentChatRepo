package com.djambulat69.fragmentchat.db

import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import io.reactivex.rxjava3.subjects.BehaviorSubject

object DataBase {
    private var _messages = listOf(
        Message(
            0, "Message text", "Author Author", mutableListOf(
                Reaction(0x1F600, 5, false)
            )
        )
    )
        get() = field.toMutableList()
    val messages: BehaviorSubject<List<Message>> = BehaviorSubject.create()

    init {
        messages.onNext(_messages)
    }

    fun sendMessage(msg: Message) {
        _messages = listOf(msg) + _messages
        messages.onNext(_messages)
    }

    fun addReactionToMessage(message: Message, emojiCode: Int) {
        val msgUpdated = message.apply {
            reactions.add(Reaction(emojiCode, 1, true))
        }
        _messages = _messages.map {
            if (it.id == msgUpdated.id)
                msgUpdated
            else
                it
        }
        messages.onNext(_messages)
    }

    fun updateReactionsInMessage(messageId: Long, updatedReactions: MutableList<Reaction>) {
        _messages.map {
            if (it.id == messageId)
                it.reactions = updatedReactions
            it
        }
        messages.onNext(_messages)
    }
}
