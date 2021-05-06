package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.djambulat69.fragmentchat.databinding.MessageOptionsBottomSheetBinding
import com.djambulat69.fragmentchat.model.network.Message
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_MESSAGE = "message"

class MessageOptionsBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: MessageOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var listener: MessageOptionsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as MessageOptionsListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MessageOptionsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = requireArguments().getSerializable(ARG_MESSAGE) as Message


        with(binding) {
            addReactionOption.setOnClickListener {
                listener?.showEmojiBottomSheetFromMessageOptions(message.id)
                dismiss()
            }

            editMessageOption.setOnClickListener {
                listener?.showEditMessageDialog(message.id, message.content)
                dismiss()
            }

            copyOption.setOnClickListener {
                listener?.copyToClipBoard(message.content)
                dismiss()
            }
        }

    }

    interface MessageOptionsListener {
        fun showEmojiBottomSheetFromMessageOptions(messageId: Int)
        fun showEditMessageDialog(messageId: Int, messageOldText: String)
        fun copyToClipBoard(text: String)
    }

    companion object {
        fun newInstance(message: Message) = MessageOptionsBottomSheetDialog().apply {
            arguments = bundleOf(
                ARG_MESSAGE to message
            )
        }
    }

}
