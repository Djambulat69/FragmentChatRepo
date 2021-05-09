package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ActivityMainBinding
import com.djambulat69.fragmentchat.ui.chat.stream.StreamChatFragment
import com.djambulat69.fragmentchat.ui.chat.topic.TopicChatFragment
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : MvpAppCompatActivity(), MainActivityView, FragmentInteractor {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var presenterProvider: Provider<MainActivityPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as FragmentChatApplication).daggerAppComponent.inject(this)

        binding = ActivityMainBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, MainFragment.newInstance())
            }
        }
    }

    override fun onNetwork(isAvailable: Boolean) {
        runOnUiThread {
            binding.networkTextDivider.isVisible = !isAvailable
            binding.networkText.isVisible = !isAvailable
        }
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
            addToBackStack(OPEN_STREAM_BACKSTACK_NAME)
            replace(
                R.id.fragment_container,
                StreamChatFragment.newInstance(streamTitle, streamId),
                StreamChatFragment::class.simpleName
            )
        }
    }

    override fun popStream() {
        supportFragmentManager.popBackStack(OPEN_STREAM_BACKSTACK_NAME, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    companion object {
        private const val OPEN_STREAM_BACKSTACK_NAME = "stream"
    }
}
