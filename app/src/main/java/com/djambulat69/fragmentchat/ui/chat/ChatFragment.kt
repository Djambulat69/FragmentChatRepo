package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"

class ChatFragment : MvpAppCompatFragment(), ChatView {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val presenter: ChatPresenter by moxyPresenter { ChatPresenter() }

    private val compositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor) {
            fragmentInteractor = context
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

        val topic = requireArguments().getSerializable(ARG_TOPIC) as Topic
        val streamTitle = requireArguments().getString(ARG_STREAM_TITLE)

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
            chatTopicTitle.text = getString(R.string.topic_title, topic.title)
            toolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }
        }

        subscribeOnSendingMessages()
        setupTextWatcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
        _binding = null
    }

    override fun showMessages(messages: List<Message>) {
        (binding.chatRecyclerView.adapter as ChatAdapter).items =
            messages.groupBy { it.date }.flatMap { (date: String, messagesbyDate: List<Message>) ->
                listOf(DateSeparatorUI(date)) + messagesToMessageUIs(messagesbyDate)
            }
        setLoading(false)
        setChatVisibility(true)
    }

    override fun showError() {
        setLoading(false)
        Snackbar.make(requireContext(), binding.root, getString(R.string.check_connection_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        setLoading(true)
        setChatVisibility(false)
    }

    private fun setLoading(isVisible: Boolean) {
        binding.messagesShimmer.isVisible = isVisible
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.chatRecyclerView.isVisible = isVisible
        binding.sendButton.isEnabled = isVisible
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

    private fun subscribeOnSendingMessages() {
        compositeDisposable.add(
            getSendButtonObservable()
                .subscribeOn(Schedulers.io())
                .subscribe { message -> presenter.sendMessage(message) }
        )
    }

    private fun setupTextWatcher() {
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
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
        })
    }

    private fun messagesToMessageUIs(messages: List<Message>) = messages.map { message ->
        val clickCallback = {
            EmojiBottomSheetDialog(requireContext()) { emojiCode ->
                presenter.addReactionToMessage(
                    message/*.copy().apply {
                    reactions = reactions.toMutableList()
                }*/, emojiCode
                )
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
        fun newInstance(topic: Topic, streamTitle: String) = ChatFragment().apply {
            arguments = bundleOf(
                ARG_TOPIC to topic,
                ARG_STREAM_TITLE to streamTitle
            )
        }
    }
}
