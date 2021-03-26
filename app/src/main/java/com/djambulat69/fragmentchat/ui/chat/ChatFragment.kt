package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChatBinding
import com.djambulat69.fragmentchat.model.Message
import com.djambulat69.fragmentchat.model.Reaction
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatAdapter
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatHolderFactory
import com.djambulat69.fragmentchat.ui.chat.recyclerview.DateSeparatorUI
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"

class ChatFragment : MvpAppCompatFragment(), ChatView {

    private var fragmentInteractor: FragmentInteractor? = null

    private var topic: Topic? = null
    private var streamTitle: String? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    private var watcher: TextWatcher? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            topic = it.getSerializable(ARG_TOPIC) as Topic
            streamTitle = it.getString(ARG_STREAM_TITLE)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbar.title = getString(R.string.sharp_placeholder, streamTitle)
            chatRecyclerView.adapter = ChatAdapter(ChatHolderFactory()).apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        if (positionStart != 0) {
                            chatRecyclerView.adapter?.let { chatRecyclerView.scrollToPosition(it.itemCount - 1) }
                        }
                    }
                })
            }
            chatTopicTitle.text = getString(R.string.topic_title, topic?.title)
            toolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }
        }

        presenter.observeMessages()
        presenter.observeSending(getSendButtonObservable())
        setupTextWatcher()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dispose()
        binding.messageEditText.removeTextChangedListener(watcher)
        _binding = null
    }

    override fun showMessages(messages: List<Message>) {
        (binding.chatRecyclerView.adapter as ChatAdapter).items =
            messages.groupBy { it.date }.flatMap { (date: String, messagesbyDate: List<Message>) ->
                listOf(DateSeparatorUI(date)) + messagesToMessageUIs(messagesbyDate)
            }
    }

    private fun getSendButtonObservable(): Observable<Message> = Observable.create { emitter ->
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

    private fun setupTextWatcher() {
        watcher = object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                binding.sendButton.isVisible = text?.isBlank() == false
                binding.addFileButton.isVisible = text?.isBlank() == true
            }

            override fun afterTextChanged(editable: Editable?) {}
        }
        binding.messageEditText.addTextChangedListener(watcher)
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
            presenter.updateReactionsInMessage(message, reactions)
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
        return SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(currentTimeMillis))
    }

    companion object {
        @JvmStatic
        fun newInstance(topic: Topic, streamTitle: String) = ChatFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_TOPIC, topic)
                putString(ARG_STREAM_TITLE, streamTitle)
            }
        }
    }
}
