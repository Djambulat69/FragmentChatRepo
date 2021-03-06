package com.djambulat69.fragmentchat.ui.chat

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.DialogChangeTopicBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val ARG_MESSAGE_ID = "message_id"
private const val ARG_OLD_TOPIC = "old_topic"

class ChangeTopicDialogFragment : DialogFragment() {

    private var _binding: DialogChangeTopicBinding? = null
    private val binding: DialogChangeTopicBinding get() = _binding!!

    private val oldTopic: String by lazy { requireArguments().getString(ARG_OLD_TOPIC) as String }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogChangeTopicBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_topic)
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                val newTopic = binding.changeTopicEditText.text.toString()
                if (oldTopic != newTopic) {
                    setFragmentResult(
                        CHANGE_TOPIC_REQUEST_KEY, bundleOf(
                            MESSAGE_ID_RESULT_KEY to requireArguments().getInt(ARG_MESSAGE_ID),
                            NEW_TOPIC_RESULT_KEY to newTopic
                        )
                    )
                }
                dialog.dismiss()
            }
            .setView(binding.root)
            .create()
    }

    companion object {
        fun newInstance(messageId: Int, oldTopic: String) = ChangeTopicDialogFragment().apply {
            arguments = bundleOf(
                ARG_MESSAGE_ID to messageId,
                ARG_OLD_TOPIC to oldTopic
            )
        }

        const val CHANGE_TOPIC_REQUEST_KEY = "change_topic_request"

        const val MESSAGE_ID_RESULT_KEY = "message_id_result"
        const val NEW_TOPIC_RESULT_KEY = "new_topic_result"
    }
}
