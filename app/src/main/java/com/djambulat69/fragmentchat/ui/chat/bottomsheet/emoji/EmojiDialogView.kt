package com.djambulat69.fragmentchat.ui.chat.bottomsheet.emoji

import moxy.MvpView
import moxy.viewstate.strategy.alias.OneExecution

interface EmojiDialogView : MvpView {

    @OneExecution
    fun setResultAndClose(emojiName: String)
}
