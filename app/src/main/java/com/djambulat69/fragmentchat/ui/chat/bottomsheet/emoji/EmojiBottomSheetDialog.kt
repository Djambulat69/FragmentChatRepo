package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.utils.EmojiEnum
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import moxy.MvpBottomSheetDialogFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

private const val ARG_MESSAGE_ID = "message_id"

class EmojiBottomSheetDialog : MvpBottomSheetDialogFragment(), EmojiDialogView {

    @Inject
    lateinit var presenterProvider: Provider<EmojiDialogPresenter>

    private val presenter: EmojiDialogPresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
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
                presenter.subscribeOnClicks(getClicks())
            }

        with(recyclerView) {
            val spanCount = (layoutManager as GridLayoutManager).spanCount
            val spacing = resources.getDimension(R.dimen.padding_medium).roundToInt()
            addItemDecoration(EmojiGridItemDecoration(spanCount, spacing))
        }

    }

    override fun onDestroyView() {
        presenter.unsubscribeFromViews()
        super.onDestroyView()
    }

    override fun setResultAndClose(emojiName: String) {
        setFragmentResult(
            EMOJI_REQUEST_KEY, bundleOf(
                MESSAGE_ID_RESULT_KEY to requireArguments().getInt(ARG_MESSAGE_ID),
                EMOJI_RESULT_KEY to emojiName
            )
        )
        dismiss()
    }

    private fun createEmojiList(): List<EmojiUI> =
        EmojiEnum.values().distinctBy { it.unicode }.sortedByDescending { it.unicodeCodePoint }.map { EmojiUI(it) }


    companion object {
        fun newInstance(messageId: Int) = EmojiBottomSheetDialog().apply {
            arguments = bundleOf(ARG_MESSAGE_ID to messageId)
        }

        const val EMOJI_REQUEST_KEY = "emoji_request"

        const val MESSAGE_ID_RESULT_KEY = "message_id_result"
        const val EMOJI_RESULT_KEY = "emoji_result"
    }
}
