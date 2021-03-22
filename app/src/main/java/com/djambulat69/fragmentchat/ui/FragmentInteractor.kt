package com.djambulat69.fragmentchat.ui

import com.djambulat69.fragmentchat.model.Topic

interface FragmentInteractor {
    fun back()
    fun openTopic(topic: Topic, streamTitle: String)
}
