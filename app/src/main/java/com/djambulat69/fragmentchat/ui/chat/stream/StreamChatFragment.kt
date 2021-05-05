package com.djambulat69.fragmentchat.ui.chat.stream

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentStreamChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.getScrollObservable
import com.djambulat69.fragmentchat.ui.chat.messagesToMessageUIs
import com.djambulat69.fragmentchat.ui.chat.recyclerview.*
import com.djambulat69.fragmentchat.ui.chat.registerAutoScrollAdapterDataObserver
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class StreamChatFragment : MvpAppCompatFragment(), StreamChatView, EmojiBottomSheetDialog.EmojiBottomDialogListener {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentStreamChatBinding? = null
    private val binding: FragmentStreamChatBinding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<StreamChatPresenter>

    private val presenter: StreamChatPresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStreamChatBinding.inflate(inflater)

        presenter.initParameters(
            requireArguments().getString(ARG_STREAM_TITLE) as String,
            requireArguments().getInt(ARG_STREAM_ID)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val streamTitle = requireArguments().getString(ARG_STREAM_TITLE)

        with(binding) {
            streamChatToolbar.title = getString(R.string.sharp_placeholder, streamTitle)
            streamChatRecyclerView.adapter =
                AsyncAdapter(
                    ChatHolderFactory(Glide.with(this@StreamChatFragment)),
                    ChatDiffCallback,
                    ChatClickMapper()
                ).apply {
                    registerAutoScrollAdapterDataObserver(binding.streamChatRecyclerView)
                }

            streamChatToolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }

            presenter.subscribeOnClicks(
                (streamChatRecyclerView.adapter as AsyncAdapter<*>).getClicks()
            )
        }

        presenter.subscribeOnSendingMessages(getSendButtonObservable())
        presenter.subscribeOnScrolling(getScrollObservable(binding.streamChatRecyclerView))
        setupTextWatcher()
    }

    override fun onDestroyView() {
        presenter.unsubscribeFromViews()
        _binding = null
        super.onDestroyView()
    }

    private var lastDecoration: TopicHeadersDecoration? = null

    override fun showMessages(messages: List<Message>) {
        val uiItems: List<ViewTyped> =
            messagesToMessageUIs(messages).groupBy { it.date }.flatMap { (date: String, messageUIsByDate: List<MessageUI>) ->
                listOf(DateSeparatorUI(date)) + messageUIsByDate
            }

        (binding.streamChatRecyclerView.adapter as AsyncAdapter<ViewTyped>).items =
            if (presenter.hasMoreMessages) listOf(SpinnerUI()) + uiItems
            else uiItems

        lastDecoration?.let { binding.streamChatRecyclerView.removeItemDecoration(it) }
        val currentDecoration = TopicHeadersDecoration(
            binding.streamChatRecyclerView,
            requireContext(),
            if (presenter.hasMoreMessages) listOf(SpinnerUI()) + uiItems
            else uiItems
        )
        binding.streamChatRecyclerView.addItemDecoration(currentDecoration)
        lastDecoration = currentDecoration

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

    override fun showEmojiBottomSheet(messageId: Int) {
        EmojiBottomSheetDialog.newInstance(messageId).show(childFragmentManager, null)
    }

    override fun addReaction(messageId: Int, emojiName: String) {
        presenter.addReactionInMessage(messageId, emojiName)
    }

    private fun setLoading(isVisible: Boolean) {
        binding.includeStreamMessagesShimmer.messagesShimmer.isVisible = isVisible
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.streamChatRecyclerView.isVisible = isVisible
        binding.streamSendButton.isEnabled = isVisible
    }

    private fun getSendButtonObservable(): Observable<String> = Observable.create { emitter ->
        binding.streamSendButton.setOnClickListener {
            emitter.onNext(binding.streamMessageEditText.text.toString().trim())
            binding.streamMessageEditText.setText("")
        }
    }

    private fun setupTextWatcher() {
        binding.streamMessageEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                binding.streamSendButton.isVisible = text.isNotBlank()
                binding.streamAddFileButton.isVisible = text.isBlank()
            }
        })
    }

    companion object {
        fun newInstance(streamTitle: String, streamId: Int) = StreamChatFragment().apply {
            arguments = bundleOf(
                ARG_STREAM_TITLE to streamTitle,
                ARG_STREAM_ID to streamId
            )
        }
    }

}
