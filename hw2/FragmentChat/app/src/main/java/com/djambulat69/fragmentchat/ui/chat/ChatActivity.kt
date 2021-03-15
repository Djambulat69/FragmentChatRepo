package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import com.djambulat69.fragmentchat.databinding.ActivityChatBinding
import com.djambulat69.fragmentchat.model.Message
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class ChatActivity : MvpAppCompatActivity(), ChatView {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private var id: Long = 1
    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.chatRecyclerView.adapter = ChatAdapter(ChatHolderFactory())
    }

    private fun getSendButtonObservable(): Observable<Message> {
        return Observable.create { emitter ->
            binding.sendButton.setOnClickListener {
                id += 1
                val message = Message(
                    id,
                    binding.messageEditText.text.toString(),
                    "Edit Author",
                    emptyList()
                )
                emitter.onNext(message)
                binding.messageEditText.setText("")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.observeMessages()
        presenter.observeSending(getSendButtonObservable())
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
    }

    override fun showMessages(messages: List<Message>) {
        (binding.chatRecyclerView.adapter as ChatAdapter).items =
            messages.map { MessageUI(it, "Edit Author") }
    }
}
