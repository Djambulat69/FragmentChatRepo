package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.djambulat69.fragmentchat.databinding.ActivityChatBinding
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : MvpAppCompatActivity(), ChatView {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }
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
                val message = Message(
                    UUID.randomUUID().toString(),
                    binding.messageEditText.text.toString().trim(),
                    "Edit Author",
                    mutableListOf(),
                    getCurrentTime()
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
            messages.groupBy { it.date }.flatMap { (date: String, messagesbyDate: List<Message>) ->
                messagesToMessageUIs(messagesbyDate) + DateSeparatorUI(date)
            }
    }

    private fun messagesToMessageUIs(messages: List<Message>) = messages.map { message ->
        val clickCallback = {
            EmojiBottomSheetDialog(this) { emojiCode ->
                presenter.addReactionToMessage(message.copy().apply {
                    reactions = reactions.toMutableList()
                }, emojiCode)
            }.show()
        }
        val reactionUpdateCallback = { reactions: MutableList<Reaction> ->
            presenter.updateReactionsInMessage(message.copy().apply {
                this.reactions = this.reactions.toMutableList()
            }, reactions)
        }

        MessageUI(
            message,
            "Edit Author",
            clickCallback,
            reactionUpdateCallback
        )
    }

    private fun getCurrentTime(): String {
        val currentTimeMillis = GregorianCalendar.getInstance().timeInMillis
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(currentTimeMillis))
    }
}
