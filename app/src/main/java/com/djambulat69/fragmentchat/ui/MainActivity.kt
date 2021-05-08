package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ActivityMainBinding
import com.djambulat69.fragmentchat.model.network.NetworkChecker
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

        val isConnected = NetworkChecker.isConnected()
        binding.networkTextDivider.isVisible = !isConnected
        binding.networkText.isVisible = !isConnected
    }

    override fun onNetworkAvailable() {
        runOnUiThread {
            binding.networkTextDivider.isVisible = false
            binding.networkText.isVisible = false
        }
    }

    override fun onNetworkLost() {
        runOnUiThread {
            binding.networkTextDivider.isVisible = true
            binding.networkText.isVisible = true
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
            addToBackStack(null)
            replace(R.id.fragment_container, StreamChatFragment.newInstance(streamTitle, streamId))
        }
    }
}
