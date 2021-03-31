package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiBottomRecyclerAdapter
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiHolderFactory
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val ARG_MESSAGE_ID = "message_id"

class EmojiBottomSheetDialog : BottomSheetDialogFragment() {

    private var listener: EmojiBottomDialogListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = parentFragment as EmojiBottomDialogListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.emoji_bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.emoji_bottom_recycler_view)?.adapter =
            EmojiBottomRecyclerAdapter(EmojiHolderFactory(), createEmojiList())
    }

    // Позже уберу это
    private fun createEmojiList() = mutableListOf<ViewTyped>().apply {
        var code = 0x1F600
        for (i in 0..59) {
            add(EmojiUI(code) { clickedEmojiCode ->
                listener!!.emojiClicked(requireArguments().getString(ARG_MESSAGE_ID)!!, clickedEmojiCode)
                dismiss()
            })
            code += 0x00001
        }
    }

    interface EmojiBottomDialogListener {
        fun emojiClicked(messageId: String, emojiCode: Int)
    }

    companion object {
        fun newInstance(messageId: String) = EmojiBottomSheetDialog().apply {
            arguments = bundleOf(ARG_MESSAGE_ID to messageId)
        }
    }
}
