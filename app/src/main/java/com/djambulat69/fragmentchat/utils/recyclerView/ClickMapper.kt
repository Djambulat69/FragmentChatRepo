package com.djambulat69.fragmentchat.utils.recyclerView

abstract class ClickMapper<out CT : ClickTypes> {
    abstract fun map(itemClick: ItemClick, items: List<ViewTyped>): CT
}
