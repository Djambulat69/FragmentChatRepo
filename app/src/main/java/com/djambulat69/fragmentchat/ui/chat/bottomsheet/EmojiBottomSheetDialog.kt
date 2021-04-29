package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.utils.EmojiEnum
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlin.math.roundToInt

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

        val recyclerView = view.findViewById<RecyclerView>(R.id.emoji_bottom_recycler_view)

        recyclerView?.adapter =
            AsyncAdapter(EmojiHolderFactory(), EmojiDiffCallback, EmojiClickMapper()).apply {
                items = createEmojiList()
                getClicks<EmojiClickTypes>()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is EmojiClickTypes.EmojiClick -> {
                                listener?.addReaction(requireArguments().getInt(ARG_MESSAGE_ID), it.emojiUI.emoji.nameInZulip)
                                dismiss()
                            }
                        }
                    }
            }

        with(recyclerView) {
            val spanCount = (layoutManager as GridLayoutManager).spanCount
            val spacing = resources.getDimension(R.dimen.padding_medium).roundToInt()
            addItemDecoration(EmojiGridItemDecoration(spanCount, spacing))
        }

    }

    private fun createEmojiList(): List<EmojiUI> =
        EmojiEnum.values().distinctBy { it.unicode }.sortedByDescending { it.unicodeCodePoint }.map { EmojiUI(it) }


    interface EmojiBottomDialogListener {
        fun addReaction(messageId: Int, emojiName: String)
    }

    companion object {
        fun newInstance(messageId: Int) = EmojiBottomSheetDialog().apply {
            arguments = bundleOf(ARG_MESSAGE_ID to messageId)
        }
    }
}
