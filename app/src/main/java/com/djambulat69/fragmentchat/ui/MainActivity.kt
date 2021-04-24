package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.chat.ChatFragment
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainActivityView, FragmentInteractor {

    private val presenter by moxyPresenter { MainActivityPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
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
            replace(R.id.fragment_container, ChatFragment.newInstance(topicTitle, streamTitle, streamId))
        }
    }
}
