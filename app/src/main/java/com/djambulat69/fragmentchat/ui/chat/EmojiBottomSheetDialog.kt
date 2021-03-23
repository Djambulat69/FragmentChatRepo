package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiBottomRecyclerAdapter
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiHolderFactory
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmojiBottomSheetDialog(context: Context, private val callBack: (Int) -> Unit) :
    BottomSheetDialog(context) {

    override fun show() {
        setContentView(R.layout.emoji_bottom_sheet_layout)
        findViewById<RecyclerView>(R.id.emoji_bottom_recycler_view)?.adapter =
            EmojiBottomRecyclerAdapter(EmojiHolderFactory(), createEmojiList())
        super.show()
    }

    // Позже уберу это
    private fun createEmojiList() = mutableListOf<ViewTyped>().apply {
        var code = 0x1F600
        for (i in 0..59) {
            add(EmojiUI(code) { clickedEmojiCode ->
                callBack(clickedEmojiCode)
                dismiss()
            })
            code += 0x00001
        }
    }
}
