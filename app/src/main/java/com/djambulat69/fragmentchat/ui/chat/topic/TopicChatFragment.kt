package com.djambulat69.fragmentchat.ui.chat.topic

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentTopicChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.*
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.recyclerview.*
import com.djambulat69.fragmentchat.ui.chat.stream.StreamChatFragment
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class TopicChatFragment :
    BaseChatFragment<TopicChatPresenter>(),
    TopicChatView {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentTopicChatBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<TopicChatPresenter>

    override val presenter: TopicChatPresenter by moxyPresenter { presenterProvider.get() }

    override val addFileButton: ImageButton
        get() = binding.topicAddFileButton

    private val topicTitle: String by lazy { requireArguments().getString(ARG_TOPIC) as String }
    private val streamTitle: String by lazy { requireArguments().getString(ARG_STREAM_TITLE) as String }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTopicChatBinding.inflate(layoutInflater, container, false)

        presenter.initParameters(
            requireArguments().getString(ARG_TOPIC) as String,
            requireArguments().getString(ARG_STREAM_TITLE) as String,
            requireArguments().getInt(ARG_STREAM_ID)
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            topicChatToolbar.title = getString(R.string.sharp_placeholder, streamTitle)
            topicChatRecyclerView.adapter =
                AsyncAdapter(
                    ChatHolderFactory(Glide.with(this@TopicChatFragment)),
                    ChatDiffCallback,
                    ChatClickMapper()
                ).apply {
                    registerAutoScrollAdapterDataObserver(binding.topicChatRecyclerView)
                }

            includeChatTopicTitle.chatTopicTitle.text = getString(R.string.topic_title, topicTitle)

            binding.topicSendButton.setOnClickListener {
                presenter.sendMessage(binding.topicMessageEditText.text.toString().trim())
                binding.topicMessageEditText.setText("")
            }

            presenter.subscribeOnClicks(
                (topicChatRecyclerView.adapter as AsyncAdapter<*>).getClicks()
            )
        }

        setUpAndBackNavigation()

        presenter.subscribeOnScrolling(getScrollObservable(binding.topicChatRecyclerView))
        setupTextWatcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribeFromViews()
        _binding = null
    }

    override fun showMessages(uiItems: List<ViewTyped>) {
        (binding.topicChatRecyclerView.adapter as AsyncAdapter<ViewTyped>).items =
            if (presenter.hasMoreMessages) listOf(SpinnerUI()) + uiItems
            else uiItems
    }

    override fun showError() {
        Snackbar.make(requireContext(), binding.root, getString(R.string.error_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun setLoading(visible: Boolean) {
        binding.includeTopicMessagesShimmer.messagesShimmer.isVisible = visible
        setChatVisibility(!visible)
    }

    override fun setMessageLoading(visible: Boolean) {
        binding.topicFileProgressBar.isVisible = visible
    }

    override fun attachUriToMessage(uri: String) {
        binding.topicMessageEditText.append(makeAttachFileString(uri))
    }

    override fun showEmojiBottomSheet(messageId: Int) {
        EmojiBottomSheetDialog.newInstance(messageId).show(childFragmentManager, null)
    }

    override fun showMessageOptions(message: Message) {
        MessageOptionsBottomSheetDialog.newInstance(message).show(childFragmentManager, null)
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.topicChatRecyclerView.isVisible = isVisible
        binding.topicSendButton.isEnabled = isVisible
    }

    private fun setupTextWatcher() {
        binding.topicMessageEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                binding.topicSendButton.isVisible = text.isNotBlank()
                binding.topicAddFileButton.isVisible = text.isBlank()
            }
        })
    }

    private fun setUpAndBackNavigation() {
        val openedFromStreamChat =
            requireActivity().supportFragmentManager.findFragmentByTag(StreamChatFragment::class.simpleName) != null

        binding.topicChatToolbar.setNavigationOnClickListener {
            backByCondition(openedFromStreamChat)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, true) {
            backByCondition(openedFromStreamChat)
        }
    }

    private fun backByCondition(openedFromStreamChat: Boolean) {
        if (openedFromStreamChat) {
            fragmentInteractor?.popStream()
        } else {
            fragmentInteractor?.back()
        }
    }

    companion object {
        fun newInstance(topicTitle: String, streamTitle: String, streamId: Int) = TopicChatFragment().apply {
            arguments = bundleOf(
                ARG_TOPIC to topicTitle,
                ARG_STREAM_TITLE to streamTitle,
                ARG_STREAM_ID to streamId
            )
        }
    }
}
