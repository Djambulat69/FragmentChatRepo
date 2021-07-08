package com.djambulat69.fragmentchat.ui.chat

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ActivityChatBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatClickMapper
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatDiffCallback
import com.djambulat69.fragmentchat.ui.chat.recyclerview.ChatHolderFactory
import com.djambulat69.fragmentchat.utils.copyText
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.SpinnerUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.snackbar.Snackbar
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider


class ChatActivity :
    MvpAppCompatActivity(),
    MessageOptionsBottomSheetDialog.MessageOptionsListener,
    ChatView {

    private val binding: ActivityChatBinding by lazy { ActivityChatBinding.inflate(layoutInflater) }

    private val presenter: ChatPresenter by moxyPresenter { presenterProvider.get() }

    private val streamTitle: String by lazy { intent.extras?.getString(ARG_STREAM_TITLE)!! }
    private val streamId: Int by lazy { intent.extras?.getInt(ARG_STREAM_ID)!! }
    private val topicTitle: String? by lazy { intent.extras?.getString(ARG_TOPIC) }

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>


    override fun onCreate(savedInstanceState: Bundle?) {

        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        presenter.initParameters(streamTitle, streamId, topicTitle)

        setChangeMessageTopicResultListener()
        setEditMessageTextResultListener()
        setEmojiBottomSheetResultListener()

        val getContentLauncher = registerUploadFileActivityLauncher()

        with(binding) {
            streamChatToolbar.title = getString(R.string.sharp_placeholder, streamTitle)
            streamChatRecyclerView.adapter =
                AsyncAdapter(
                    ChatHolderFactory(Glide.with(this@ChatActivity)),
                    ChatDiffCallback,
                    ChatClickMapper()
                ).apply {
                    registerAutoScrollAdapterDataObserver(binding.streamChatRecyclerView)
                }

            streamChatToolbar.setNavigationOnClickListener { finish() }

            streamSendButton.setOnClickListener {
                presenter.sendMessage(
                    streamMessageEditText.text.toString().trim(),
                    streamTopicEditText.text.toString().trim()
                )
                streamMessageEditText.setText("")
            }

            streamAddFileButton.setOnClickListener { getContentLauncher.launch(ALL_FILES_TYPE) }

            presenter.subscribeOnClicks((streamChatRecyclerView.adapter as AsyncAdapter<*>).getClicks())
        }

        presenter.subscribeOnScrolling(getScrollObservable(binding.streamChatRecyclerView))
        setupTextWatcher()
    }

    override fun onDestroy() {
        presenter.unsubscribeFromViews()
        super.onDestroy()
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

    override fun setFileLoading(visible: Boolean) {
        binding.streamFileProgressBar.isVisible = visible
    }

    override fun showEmojiBottomSheet(messageId: Int) {
        EmojiBottomSheetDialog.newInstance(messageId).show(supportFragmentManager, null)
    }

    override fun showError() {
        Snackbar.make(this, binding.root, getString(R.string.error_text), Snackbar.LENGTH_SHORT).show()
    }

    override fun showMessageOptions(message: Message) {
        MessageOptionsBottomSheetDialog.newInstance(message).show(supportFragmentManager, null)
    }

    override fun attachUriToMessage(uri: String) {
        binding.streamMessageEditText.append(makeAttachFileString(uri))
    }

    override fun showEmojiBottomSheetFromMessageOptions(messageId: Int) =
        presenter.showEmojiBottomSheet(messageId)

    override fun showEditMessageDialog(messageId: Int, messageOldText: String) {
        EditMessageDialogFragment.newInstance(messageId, messageOldText).show(supportFragmentManager, null)
    }

    override fun copyToClipBoard(text: String) {
        val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.copyText(text)

        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun deleteMessage(id: Int) {
        presenter.deleteMessage(id)
    }

    override fun showChangeTopicDialog(id: Int, oldTopic: String) {
        ChangeTopicDialogFragment.newInstance(id, oldTopic).show(supportFragmentManager, null)
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

    private fun setEmojiBottomSheetResultListener() {
        supportFragmentManager.setFragmentResultListener(
            EmojiBottomSheetDialog.EMOJI_REQUEST_KEY,
            this
        ) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(EmojiBottomSheetDialog.MESSAGE_ID_RESULT_KEY)
            val emojiName = bundle.getString(EmojiBottomSheetDialog.EMOJI_RESULT_KEY) as String

            presenter.addReactionInMessage(messageId, emojiName)
        }
    }

    private fun setEditMessageTextResultListener() {
        supportFragmentManager.setFragmentResultListener(
            EditMessageDialogFragment.EDIT_MESSAGE_REQUEST_KEY,
            this
        ) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(EditMessageDialogFragment.MESSAGE_ID_RESULT_KEY)
            val newText = bundle.getString(EditMessageDialogFragment.NEW_TEXT_RESULT_KEY) as String

            presenter.editMessageText(messageId, newText)
        }
    }

    private fun setChangeMessageTopicResultListener() {
        supportFragmentManager.setFragmentResultListener(
            ChangeTopicDialogFragment.CHANGE_TOPIC_REQUEST_KEY,
            this
        ) { _: String, bundle: Bundle ->
            val messageId = bundle.getInt(ChangeTopicDialogFragment.MESSAGE_ID_RESULT_KEY)
            val newTopic = bundle.getString(ChangeTopicDialogFragment.NEW_TOPIC_RESULT_KEY) as String

            presenter.changeMessageTopic(messageId, newTopic)
        }
    }

    private fun registerUploadFileActivityLauncher(): ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { data ->
            data?.let { uri ->
                uploadFile(uri)
            }
        }

    private fun uploadFile(uri: Uri) {
        val contentResolver = FragmentChatApplication.contentResolver()
        val type = contentResolver.getType(uri)

        contentResolver.queryNameAndSize(uri) { name: String, size: Int ->
            if (size > MEGABYTES_25_IN_BYTES) {
                Toast.makeText(this, R.string.too_big_file, Toast.LENGTH_SHORT).show()
            } else if (type != null) {
                presenter.uploadFile(uri, type, name)
            }
        }
    }

    companion object {
        const val ARG_STREAM_TITLE = "stream_title"
        const val ARG_STREAM_ID = "stream_id"
        const val ARG_TOPIC = "topic"
    }

}
