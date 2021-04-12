package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.network.Topic
import com.djambulat69.fragmentchat.ui.chat.ChatFragment

class MainActivity : AppCompatActivity(), FragmentInteractor {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragment_container, MainFragment.newInstance())
            }
        }
    }

    override fun back() {
        supportFragmentManager.popBackStack()
    }

    override fun openTopic(topic: Topic, streamTitle: String, streamId: Int) {
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.fragment_container, ChatFragment.newInstance(topic, streamTitle, streamId))
        }
    }
}
