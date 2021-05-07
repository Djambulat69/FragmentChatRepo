package com.djambulat69.fragmentchat.ui.chat.stream

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
import com.djambulat69.fragmentchat.databinding.FragmentStreamChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.chat.*
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
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
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_STREAM_TITLE = "stream_title"
private const val ARG_STREAM_ID = "stream_id"

class StreamChatFragment :
    MvpAppCompatFragment(),
    StreamChatView,
    MessageOptionsBottomSheetDialog.MessageOptionsListener {

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

        setEmojiBottomSheetResultListener()
        setEditMessageTextResultListener()
        setChangeMessageTopicResultListener()

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

    override fun showMessages(uiItems: List<ViewTyped>) {

        (binding.streamChatRecyclerView.adapter as AsyncAdapter<ViewTyped>).items =
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
        binding.includeStreamMessagesShimmer.messagesShimmer.isVisible = isVisible
    }

    private fun setChatVisibility(isVisible: Boolean) {
        binding.streamChatRecyclerView.isVisible = isVisible
        binding.streamSendButton.isEnabled = isVisible
    }

    private fun getSendButtonObservable(): Observable<Pair<String, String>> = Observable.create { emitter ->
        with(binding) {
            streamSendButton.setOnClickListener {
                emitter.onNext(
                    streamMessageEditText.text.toString().trim() to
                            streamTopicEditText.text.toString().trim()
                )
                streamMessageEditText.setText("")
            }
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

    private fun setEmojiBottomSheetResultListener() {
        setChildFragmentResultListener(EmojiBottomSheetDialog.EMOJI_REQUEST_KEY) { _: String, bundle: Bundle ->
        val messageId = bundle.getInt(EmojiBottomSheetDialog.MESSAGE_ID_RESULT_KEY)
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
        fun newInstance(streamTitle: String, streamId: Int) = StreamChatFragment().apply {
            arguments = bundleOf(
                ARG_STREAM_TITLE to streamTitle,
                ARG_STREAM_ID to streamId
            )
        }
    }

}
