package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import com.djambulat69.fragmentchat.utils.recyclerView.BaseAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

class EmojiBottomRecyclerAdapter(
    holderFactory: EmojiHolderFactory,
    override var items: List<ViewTyped>
) : BaseAdapter(holderFactory)
