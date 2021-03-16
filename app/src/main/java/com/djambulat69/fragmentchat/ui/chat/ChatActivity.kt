package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.djambulat69.fragmentchat.databinding.ActivityChatBinding
import com.djambulat69.fragmentchat.model.Message
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class ChatActivity : MvpAppCompatActivity(), ChatView {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private var id: Long = 0
    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.chatRecyclerView.adapter = ChatAdapter(ChatHolderFactory())
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text?.isNotBlank() == true) {
                    binding.sendButton.visibility = View.VISIBLE
                    binding.addFileButton.visibility = View.INVISIBLE
                } else {
                    binding.sendButton.visibility = View.INVISIBLE
                    binding.addFileButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun getSendButtonObservable(): Observable<Message> {
        return Observable.create { emitter ->
            binding.sendButton.setOnClickListener {
                id += 1
                val message = Message(
                    id,
                    binding.messageEditText.text.toString().trim(),
                    "Edit Author",
                    mutableListOf()
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
            messages.map { message ->
                MessageUI(message, "Edit Author", {
                    EmojiBottomSheetDialog(this) { emojiCode ->
                        presenter.addReactionToMessage(message.copy(), emojiCode)
                    }.show()
                }) { reactions, msgId ->
                    presenter.updateReactionsInMessage(msgId, reactions)
                }
            }
    }
}
