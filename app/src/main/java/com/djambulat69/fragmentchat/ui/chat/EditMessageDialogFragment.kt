package com.djambulat69.fragmentchat.ui.chat

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.DialogEditMessageBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val ARG_MESSAGE_ID = "message_id"
private const val ARG_MESSAGE_OLD_TEXT = "message_old_text"

class EditMessageDialogFragment : DialogFragment() {

    private var _binding: DialogEditMessageBinding? = null
    private val binding get() = _binding!!

    private var listener: EditMessageDialogListener? = null

    private val oldText: String by lazy { requireArguments().getString(ARG_MESSAGE_OLD_TEXT) as String }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as EditMessageDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditMessageBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireParentFragment().requireContext())
            .setTitle(R.string.edit_message)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                val newText = binding.editMessageEditText.text.toString()
                if (oldText != newText) {
                    listener?.editMessage(
                        requireArguments().getInt(ARG_MESSAGE_ID),
                        binding.editMessageEditText.text.toString()
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

    interface EditMessageDialogListener {
        fun editMessage(messageId: Int, newText: String)
    }

    companion object {
        fun newInstance(messageId: Int, messageOldText: String) = EditMessageDialogFragment().apply {
            arguments = bundleOf(
                ARG_MESSAGE_ID to messageId,
                ARG_MESSAGE_OLD_TEXT to messageOldText
            )
        }
    }

}
