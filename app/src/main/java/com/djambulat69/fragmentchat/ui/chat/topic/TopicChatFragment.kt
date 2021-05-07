package com.djambulat69.fragmentchat.ui.chat.topic

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentTopicChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.NetworkListener
import com.djambulat69.fragmentchat.ui.chat.*
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.recyclerview.*
import com.djambulat69.fragmentchat.utils.copyText
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.setChildFragmentResultListener
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_TOPIC = "topic"
private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class TopicChatFragment :
    MvpAppCompatFragment(),
    TopicChatView,
    MessageOptionsBottomSheetDialog.MessageOptionsListener,
    NetworkListener {

    private var fragmentInteractor: FragmentInteractor? = null

    private var _binding: FragmentTopicChatBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<TopicChatPresenter>

    private val presenter: TopicChatPresenter by moxyPresenter { presenterProvider.get() }

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

        val topicTitle = requireArguments().getString(ARG_TOPIC)
        val streamTitle = requireArguments().getString(ARG_STREAM_TITLE)

        setEmojiBottomSheetResultListener()
        setEditMessageTextResultListener()
        setChangeMessageTopicResultListener()

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

            chatTopicTitle.text = getString(R.string.topic_title, topicTitle)
            topicChatToolbar.setNavigationOnClickListener {
                fragmentInteractor?.back()
            }

            presenter.subscribeOnClicks(
                (topicChatRecyclerView.adapter as AsyncAdapter<*>).getClicks()
            )
        }
        presenter.subscribeOnSendingMessages(getSendButtonObservable())
        presenter.subscribeOnScrolling(getScrollObservable(binding.topicChatRecyclerView))
        setupTextWatcher()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribeFromViews()
        _binding = null
    }

    override fun showMessages(messages: List<Message>) {
        val uiItems: List<ViewTyped> = messagesByDate(messages, false)

        (binding.topicChatRecyclerView.adapter as AsyncAdapter<ViewTyped>).items =
            if (presenter.hasMoreMessages) listOf(SpinnerUI()) + uiItems
            else uiItems

        setLoading(false)
        setChatVisibility(true)
    }

    override fun showError() {
        Snackbar.make(requireContext(), binding.root, getString(R.string.error_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        setLoading(true)
        setChatVisibility(false)
    }

    override fun showEmojiBottomSheet(messageId: Int) {
        EmojiBottomSheetDialog.newInstance(messageId).show(childFragmentManager, null)
    }

    override fun showMessageOptions(message: Message) {
        MessageOptionsBottomSheetDialog.newInstance(message).show(childFragmentManager, null)
    }

    override fun showEmojiBottomSheetFromMessageOptions(messageId: Int) {
        showEmojiBottomSheet(messageId)
    }

    override fun showEditMessageDialog(messageId: Int, messageOldText: String) {
        EditMessageDialogFragment.newInstance(messageId, messageOldText).show(childFragmentManager, null)
    }

    override fun onAvailable() {
        presenter.updateMessages()
    }

    override fun copyToClipBoard(text: String) {
        val clipBoard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.copyText(text)

        Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun deleteMessage(id: Int) {
        presenter.deleteMessage(id)
    }

    override fun showChangeTopicDialog(id: Int, oldTopic: String) {
        ChangeTopicDialogFragment.newInstance(id, oldTopic).show(childFragmentManager, null)
    }

    private fun setLoading(isVisible: Boolean) {
        binding.includeTopicMessagesShimmer.messagesShimmer.isVisible = isVisible
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.topicChatRecyclerView.isVisible = isVisible
        binding.topicSendButton.isEnabled = isVisible
    }

    private fun getSendButtonObservable(): Observable<String> = Observable.create { emitter ->
        binding.topicSendButton.setOnClickListener {
            emitter.onNext(binding.topicMessageEditText.text.toString().trim())
            binding.topicMessageEditText.setText("")
        }
    }

    private fun setupTextWatcher() {
        binding.topicMessageEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                binding.topicSendButton.isVisible = text.isNotBlank()
                binding.topicAddFileButton.isVisible = text.isBlank()
            }
        })
    }

    private fun setEmojiBottomSheetResultListener() {
        setChildFragmentResultListener(EmojiBottomSheetDialog.EMOJI_REQUEST_KEY) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(EmojiBottomSheetDialog.EMOJI_RESULT_KEY)
            val emojiName = bundle.getString(EmojiBottomSheetDialog.EMOJI_RESULT_KEY) as String

            presenter.addReactionInMessage(messageId, emojiName)
        }
    }

    private fun setEditMessageTextResultListener() {
        setChildFragmentResultListener(EditMessageDialogFragment.EDIT_MESSAGE_REQUEST_KEY) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(EditMessageDialogFragment.MESSAGE_ID_RESULT_KEY)
            val newText = bundle.getString(EditMessageDialogFragment.NEW_TEXT_RESULT_KEY) as String

            presenter.editMessageText(messageId, newText)
        }
    }

    private fun setChangeMessageTopicResultListener() {
        setChildFragmentResultListener(ChangeTopicDialogFragment.CHANGE_TOPIC_REQUEST_KEY) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(ChangeTopicDialogFragment.MESSAGE_ID_RESULT_KEY)
            val newTopic = bundle.getString(ChangeTopicDialogFragment.NEW_TOPIC_RESULT_KEY) as String

            presenter.changeMessageTopic(messageId, newTopic)
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
