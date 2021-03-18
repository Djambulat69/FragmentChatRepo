package com.djambulat69.fragmentchat.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.databinding.FragmentChatBinding
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatAdapter
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatHolderFactory
import com.djambulat69.fragmentchat.ui.chat.recyclerview.DateSeparatorUI
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : MvpAppCompatFragment(), ChatView {

    private lateinit var binding: FragmentChatBinding
    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    override fun onStart() {
        super.onStart()
        presenter.observeMessages()
        presenter.observeSending(getSendButtonObservable())
    }

    override fun onStop() {
        super.onStop()
        presenter.dispose()
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

    override fun showMessages(messages: List<Message>) {
        (binding.chatRecyclerView.adapter as ChatAdapter).items =
            messages.groupBy { it.date }.flatMap { (date: String, messagesbyDate: List<Message>) ->
                messagesToMessageUIs(messagesbyDate) + DateSeparatorUI(date)
            }
    }

    private fun messagesToMessageUIs(messages: List<Message>) = messages.map { message ->
        val clickCallback = {
            EmojiBottomSheetDialog(requireContext()) { emojiCode ->
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

    companion object {
        @JvmStatic
        fun newInstance() = ChatFragment()
    }
}
