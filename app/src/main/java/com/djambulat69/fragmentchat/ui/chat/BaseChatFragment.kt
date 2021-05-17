package com.djambulat69.fragmentchat.ui.chat

import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.MessageOptionsBottomSheetDialog
import com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji.EmojiBottomSheetDialog
import com.djambulat69.fragmentchat.utils.copyText
import com.djambulat69.fragmentchat.utils.setChildFragmentResultListener
import moxy.MvpAppCompatFragment

abstract class BaseChatFragment<P : BaseChatPresenter<*, *>> :
    MvpAppCompatFragment(),
    MessageOptionsBottomSheetDialog.MessageOptionsListener {

    abstract val presenter: P

    abstract val addFileButton: ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setChangeMessageTopicResultListener()
        setEditMessageTextResultListener()
        setEmojiBottomSheetResultListener()

        val getContentLauncher = registerUploadFileActivityLauncher()
        addFileButton.setOnClickListener { getContentLauncher.launch(ALL_FILES_TYPE) }

    }


    override fun showEmojiBottomSheetFromMessageOptions(messageId: Int) =
        presenter.showEmojiBottomSheet(messageId)

    override fun showEditMessageDialog(messageId: Int, messageOldText: String) {
        EditMessageDialogFragment.newInstance(messageId, messageOldText).show(childFragmentManager, null)
    }

    override fun copyToClipBoard(text: String) {
        val clipBoard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipBoard.copyText(text)

        Toast.makeText(requireContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun deleteMessage(id: Int) =
        presenter.deleteMessage(id)

    override fun showChangeTopicDialog(id: Int, oldTopic: String) {
        ChangeTopicDialogFragment.newInstance(id, oldTopic).show(childFragmentManager, null)
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
                Toast.makeText(requireContext(), R.string.too_big_file, Toast.LENGTH_SHORT).show()
            } else if (type != null) {
                presenter.uploadFile(uri, type, name)
            }
        }
    }
}
