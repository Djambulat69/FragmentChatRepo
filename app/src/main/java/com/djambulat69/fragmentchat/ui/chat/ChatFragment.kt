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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChatBinding
import com.djambulat69.fragmentchat.model.db.FragmentChatDatabase
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatAdapter
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatHolderFactory
import com.djambulat69.fragmentchat.ui.chat.recyclerview.DateSeparatorUI
import com.djambulat69.fragmentchat.ui.chat.recyclerview.MessageUI
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class ChatFragment : MvpAppCompatFragment(), ChatView, EmojiBottomSheetDialog.EmojiBottomDialogListener {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val presenter: ChatPresenter by moxyPresenter {
        ChatPresenter(
            requireArguments().getSerializable(ARG_TOPIC) as Topic,
            requireArguments().getString(ARG_STREAM_TITLE) as String,
            requireArguments().getInt(ARG_STREAM_ID),
            ChatRepository(FragmentChatDatabase.get(requireContext().applicationContext).messagesDao())
        )
    }

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

        val topic: Topic = requireArguments().getSerializable(ARG_TOPIC) as Topic
        val streamTitle = requireArguments().getString(ARG_STREAM_TITLE)

        with(binding) {
            toolbar.title = getString(R.string.sharp_placeholder, streamTitle)
            chatRecyclerView.adapter = ChatAdapter(ChatHolderFactory(Glide.with(this@ChatFragment))).apply {
                registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        super.onItemRangeInserted(positionStart, itemCount)
                        if (presenter.shouldScrollBottom) {
                            chatRecyclerView.adapter?.let { chatRecyclerView.scrollToPosition(it.itemCount - 1) }
                            presenter.shouldScrollBottom = false
                        }
                    }
                })
            }
            chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!presenter.isNextPageLoading && presenter.hasMoreMessages && recyclerView.adapter != null) {

                        val itemsRemaining = (recyclerView.layoutManager as LinearLayoutManager)
                            .findFirstCompletelyVisibleItemPosition()

                        if (itemsRemaining < 5) {
                            presenter.isNextPageLoading = true
                            val lastLoadedMessageId =
                                (recyclerView.adapter as ChatAdapter).items.first { uiItem -> uiItem is MessageUI }.id.toLong()
                            presenter.getNextMessages(lastLoadedMessageId)
                        }
                    }
                }
            })
            chatTopicTitle.text = getString(R.string.topic_title, topic.name)
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
        val uiItemsToAdd: MutableList<ViewTyped> =
            messagesToMessageUIs(messages).groupBy { it.date }.flatMap { (date: String, messageUIsByDate: List<MessageUI>) ->
                listOf(DateSeparatorUI(date)) + messageUIsByDate
            } as MutableList<ViewTyped>
        if (presenter.hasMoreMessages) uiItemsToAdd.add(0, SpinnerUI())
        (binding.chatRecyclerView.adapter as ChatAdapter).items = uiItemsToAdd

        setLoading(false)
        setChatVisibility(true)
    }

    override fun showError() {
        Snackbar.make(requireContext(), binding.root, getString(R.string.check_connection_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        setLoading(true)
        setChatVisibility(false)
    }

    override fun addReaction(messageId: Int, emojiName: String) {
        presenter.addReactionInMessage(messageId, emojiName)
    }

    private fun setLoading(isVisible: Boolean) {
        binding.messagesShimmer.isVisible = isVisible
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.chatRecyclerView.isVisible = isVisible
        binding.sendButton.isEnabled = isVisible
    }

    private fun getSendButtonObservable(): Observable<String> = Observable.create { emitter ->
        binding.sendButton.setOnClickListener {
            emitter.onNext(binding.messageEditText.text.toString().trim())
            binding.messageEditText.setText("")
        }
    }

    private fun subscribeOnSendingMessages() {
        compositeDisposable.add(
            getSendButtonObservable()
                .subscribeOn(Schedulers.io())
                .subscribe { messageText -> presenter.sendMessage(messageText) }
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
        val longClickCallback = {
            EmojiBottomSheetDialog.newInstance(message.id).show(childFragmentManager, null)
        }
        val reactionUpdateCallback = { isSet: Boolean, messageId: Int, emojiName: String ->
            if (isSet) {
                presenter.addReactionInMessage(messageId, emojiName)
            } else {
                presenter.removeReactionInMessage(messageId, emojiName)
            }
        }

        MessageUI(
            message,
            longClickCallback,
            reactionUpdateCallback
        )
    }

    companion object {
        fun newInstance(topic: Topic, streamTitle: String, streamId: Int) = ChatFragment().apply {
            arguments = bundleOf(
                ARG_TOPIC to topic,
                ARG_STREAM_TITLE to streamTitle,
                ARG_STREAM_ID to streamId
            )
        }
    }
}
