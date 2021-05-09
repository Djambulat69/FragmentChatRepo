package com.djambulat69.fragmentchat.ui

interface FragmentInteractor {
    fun back()
    fun openTopic(topicTitle: String, streamTitle: String, streamId: Int)
    fun openStream(streamTitle: String, streamId: Int)
    fun popStream()
}
