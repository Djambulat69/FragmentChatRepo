package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import android.os.Bundle
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
import com.djambulat69.fragmentchat.ui.NetworkListener
import com.djambulat69.fragmentchat.ui.chat.recyclerview.*
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*
import java.util.concurrent.TimeUnit

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

private const val MESSAGES_PREFETCH_DISTANCE = 5
private const val SCROLL_EMIT_DEBOUNCE_MILLIS = 1000L
private const val MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL = 2

class ChatFragment : MvpAppCompatFragment(), ChatView, EmojiBottomSheetDialog.EmojiBottomDialogListener, NetworkListener {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val presenter: ChatPresenter by moxyPresenter {
        ChatPresenter(
            requireArguments().getSerializable(ARG_TOPIC) as Topic,
            requireArguments().getString(ARG_STREAM_TITLE) as String,
            requireArguments().getInt(ARG_STREAM_ID),
            ChatRepository(FragmentChatDatabase.INSTANCE.messagesDao())
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
            chatRecyclerView.adapter =
                AsyncAdapter(ChatHolderFactory(Glide.with(this@ChatFragment)), ChatDiffCallback).apply {
                    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)

                            chatRecyclerView.adapter?.let {
                                if (positionStart > MIN_INSERTED_ITEMS_POSITION_TO_AUTOSCROLL) {
                                    chatRecyclerView.scrollToPosition(it.itemCount - 1)
                                }
                            }
                        }
                    })
                }

            chatTopicTitle.text = getString(R.string.topic_title, topic.name)
            toolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }
        }

        subscribeOnSendingMessages()
        subscribeOnScrolling()
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
        (binding.chatRecyclerView.adapter as AsyncAdapter).items = uiItemsToAdd

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

    override fun onAvailable() {
        presenter.updateMessages()
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

    private fun getScrollObservable() = Observable.create<Long> { emitter ->
        binding.chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                loadNextPage(recyclerView)
            }

            private fun loadNextPage(recyclerView: RecyclerView) {
                if (recyclerView.adapter != null) {

                    val itemsRemaining = (recyclerView.layoutManager as LinearLayoutManager)
                        .findFirstCompletelyVisibleItemPosition()

                    if (itemsRemaining < MESSAGES_PREFETCH_DISTANCE && itemsRemaining != RecyclerView.NO_POSITION) {
                        val lastLoadedMessageId =
                            (recyclerView.adapter as AsyncAdapter).items.first { uiItem -> uiItem is MessageUI }.id.toLong()
                        emitter.onNext(lastLoadedMessageId)
                    }
                }
            }
        })
    }

    private fun subscribeOnScrolling() {
        compositeDisposable.add(
            getScrollObservable()
                .subscribeOn(Schedulers.io())
                .debounce(SCROLL_EMIT_DEBOUNCE_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe { anchor ->
                    presenter.getNextMessages(anchor)
                }
        )
    }

    private fun setupTextWatcher() {
        binding.messageEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                binding.sendButton.isVisible = text.isNotBlank()
                binding.addFileButton.isVisible = text.isBlank()
            }
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
