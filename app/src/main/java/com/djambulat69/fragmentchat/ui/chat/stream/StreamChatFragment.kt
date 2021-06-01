package com.djambulat69.fragmentchat.ui.chat.stream

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentStreamChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.*
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.recyclerview.*
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.viewBinding
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class StreamChatFragment :
    BaseChatFragment<StreamChatPresenter, FragmentStreamChatBinding>(),
    StreamChatView {

    private var fragmentInteractor: FragmentInteractor? = null

    override val binding: FragmentStreamChatBinding by viewBinding { FragmentStreamChatBinding.inflate(layoutInflater) }

    @Inject
    lateinit var presenterProvider: Provider<StreamChatPresenter>


    private val streamTitle: String by lazy { requireArguments().getString(ARG_STREAM_TITLE) as String }
    private val streamId: Int by lazy { requireArguments().getInt(ARG_STREAM_ID) }


    override val presenter: StreamChatPresenter by moxyPresenter { presenterProvider.get() }

    override val addFileButton: ImageButton
        get() = binding.streamAddFileButton


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter.initParameters(
            streamTitle, streamId
        )

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

            streamSendButton.setOnClickListener {
                presenter.sendMessage(
                    streamMessageEditText.text.toString().trim(),
                    streamTopicEditText.text.toString().trim()
                )
                streamMessageEditText.setText("")
            }

            presenter.subscribeOnClicks(
                (streamChatRecyclerView.adapter as AsyncAdapter<*>).getClicks()
            )
        }

        presenter.subscribeOnScrolling(getScrollObservable(binding.streamChatRecyclerView))
        setupTextWatcher()
    }


    override fun onDestroyView() {
        presenter.unsubscribeFromViews()
        super.onDestroyView()
    }

    override fun showMessages(uiItems: List<ViewTyped>) {

        (binding.streamChatRecyclerView.adapter as AsyncAdapter<ViewTyped>).items =
            if (presenter.hasMoreMessages) listOf(SpinnerUI()) + uiItems
            else uiItems

    }

    override fun setLoading(visible: Boolean) {
        binding.includeStreamMessagesShimmer.messagesShimmer.isVisible = visible
        setChatVisibility(!visible)
    }

    override fun setMessageLoading(visible: Boolean) {
        binding.streamFileProgressBar.isVisible = visible
    }

    override fun showEmojiBottomSheet(messageId: Int) {
        EmojiBottomSheetDialog.newInstance(messageId).show(childFragmentManager, null)
    }

    override fun showError() {
        Snackbar.make(requireContext(), binding.root, getString(R.string.error_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun showMessageOptions(message: Message) {
        MessageOptionsBottomSheetDialog.newInstance(message).show(childFragmentManager, null)
    }

    override fun openTopicChat(topicTitle: String) {
        fragmentInteractor?.openTopic(topicTitle, streamTitle, streamId)
    }

    override fun attachUriToMessage(uri: String) {
        binding.streamMessageEditText.append(makeAttachFileString(uri))
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.streamChatRecyclerView.isVisible = isVisible
        binding.streamSendButton.isEnabled = isVisible
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
