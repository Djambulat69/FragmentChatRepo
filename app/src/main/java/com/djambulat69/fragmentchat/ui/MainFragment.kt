package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.chat.ChatFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.main_fragment_container, ChannelsFragment.newInstance())
            }
        }

        val bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> {
                    childFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChannelsFragment.newInstance())
                    }
                    true
                }
                R.id.people_menu_item -> {
                    childFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChatFragment.newInstance())
                    }
                    true
                }
                R.id.profile_menu_item -> {
                    childFragmentManager.commit {
                        replace(R.id.main_fragment_container, ChannelsFragment.newInstance())
                    }
                    true
                }
                else -> false
            }
        }
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
