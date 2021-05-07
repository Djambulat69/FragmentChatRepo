package com.djambulat69.fragmentchat.ui.chat

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.DialogEditMessageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val ARG_MESSAGE_ID = "message_id"
private const val ARG_MESSAGE_OLD_TEXT = "message_old_text"

class EditMessageDialogFragment : DialogFragment() {

    private var _binding: DialogEditMessageBinding? = null
    private val binding get() = _binding!!

    private val oldText: String by lazy { requireArguments().getString(ARG_MESSAGE_OLD_TEXT) as String }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditMessageBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireParentFragment().requireContext())
            .setTitle(R.string.edit_message)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                val newText = binding.editMessageEditText.text.toString()
                if (oldText != newText) {
                    setFragmentResult(
                        EDIT_MESSAGE_REQUEST_KEY, bundleOf(
                            MESSAGE_ID_RESULT_KEY to requireArguments().getInt(ARG_MESSAGE_ID),
                            NEW_TEXT_RESULT_KEY to newText
                        )
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .setView(binding.root)
            .create()
    }

    companion object {
        fun newInstance(messageId: Int, messageOldText: String) = EditMessageDialogFragment().apply {
            arguments = bundleOf(
                ARG_MESSAGE_ID to messageId,
                ARG_MESSAGE_OLD_TEXT to messageOldText
            )
        }

        const val EDIT_MESSAGE_REQUEST_KEY = "edit_message_request"

        const val MESSAGE_ID_RESULT_KEY = "message_id_result"
        const val NEW_TEXT_RESULT_KEY = "new_text_result"
    }

}
