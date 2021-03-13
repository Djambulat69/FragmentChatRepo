package com.djambulat69.fragmentchat.db

import com.djambulat69.fragmentchat.model.Message
import io.reactivex.rxjava3.subjects.BehaviorSubject

object DataBase {
    private val _messages = mutableListOf(Message(0, "Message text", "Author Author", emptyList()))
    val messages: BehaviorSubject<List<Message>> = BehaviorSubject.create()

    init {
        messages.onNext(_messages)
    }

    fun sendMessage(msg: Message) {
        _messages.add(0, msg)
        messages.onNext(_messages)
    }
}
