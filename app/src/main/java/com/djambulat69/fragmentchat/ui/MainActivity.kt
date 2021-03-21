package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.chat.ChatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_fragment_container, ChannelsFragment.newInstance())
            }
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChannelsFragment.newInstance())
                    }
                    true
                }
                R.id.people_menu_item -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChatFragment.newInstance())
                    }
                    true
                }
                R.id.profile_menu_item -> {
                    supportFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChannelsFragment.newInstance())
                    }
                    true
                }
                else -> false
            }
        }
    }
}
