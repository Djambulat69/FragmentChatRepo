package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.chat.stream.StreamChatFragment
import com.djambulat69.fragmentchat.ui.chat.topic.TopicChatFragment
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : MvpAppCompatActivity(), MainActivityView, FragmentInteractor {

    @Inject
    lateinit var presenterProvider: Provider<MainActivityPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as FragmentChatApplication).daggerAppComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, MainFragment.newInstance())
            }
        }
    }

    override fun onNetworkAvailable() {
        supportFragmentManager.fragments.filterIsInstance<NetworkListener>().forEach { it.onAvailable() }
    }

    override fun back() {
        supportFragmentManager.popBackStack()
    }

    override fun openTopic(topicTitle: String, streamTitle: String, streamId: Int) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, TopicChatFragment.newInstance(topicTitle, streamTitle, streamId))
        }
    }

    override fun openStream(streamTitle: String, streamId: Int) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, StreamChatFragment.newInstance(streamTitle, streamId))
        }
    }
}
