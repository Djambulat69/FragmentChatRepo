package com.djambulat69.fragmentchat.utils.recyclerView

import com.djambulat69.fragmentchat.R

class SpinnerUI : ViewTyped {

    override val id: String = "PROGRESSBAR_SPINNER_ID"
    override val click: (() -> Unit)? = null
    override val viewType: Int = R.layout.loading_header

}
