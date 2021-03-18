package com.djambulat69.fragmentchat.ui.chat

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.chat.recyclerview.EmojiBottomRecyclerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class EmojiBottomSheetDialog(context: Context, private val callBack: (Int) -> Unit) :
    BottomSheetDialog(context) {
    override fun show() {
        setContentView(R.layout.emoji_bottom_sheet_layout)
        findViewById<RecyclerView>(R.id.emoji_bottom_recycler_view)?.apply {
            adapter = EmojiBottomRecyclerAdapter(
                mutableListOf<Int>().apply {
                    var code = 0x1F600
                    for (i in 0..40) {
                        add(code)
                        code += 0x00001
                    }
                }
            ) {
                callBack(it)
                dismiss()
            }
        }

        super.show()
    }
}
