package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import com.djambulat69.fragmentchat.databinding.ActivityChatBinding
import com.djambulat69.fragmentchat.model.Message
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class ChatActivity : MvpAppCompatActivity(), ChatView {

    private lateinit var binding: ActivityChatBinding
    private var id: Long = 1

    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chatRecyclerView.adapter = ChatRecyclerAdapter(emptyList())
        val buttonObservable = Observable.create<Message> { emitter ->
            binding.sendButton.setOnClickListener {
                id += 1
                val message = Message(
                    id,
                    binding.messageEditText.text.toString(),
                    "Edit Author",
                    emptyList()
                )
                binding.messageEditText.setText("")
                emitter.onNext(message)
            }
        }
        presenter.observeMessages()
        presenter.observeSending(buttonObservable)
    }

    override fun showMessages(messages: List<Message>) {
        (binding.chatRecyclerView.adapter as ChatRecyclerAdapter).setMessages(messages)
    }
}
