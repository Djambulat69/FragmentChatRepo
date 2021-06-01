package com.djambulat69.fragmentchat.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.people.PeopleFragment
import com.djambulat69.fragmentchat.ui.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment(), FragmentInteractor {

    private var fragmentInteractor: FragmentInteractor? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                add(R.id.main_fragment_container, ChannelsFragment.newInstance(), BottomNavigationPages.CHANNELS.name)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> openFragment(BottomNavigationPages.CHANNELS)
                R.id.people_menu_item -> openFragment(BottomNavigationPages.PEOPLE)
                R.id.profile_menu_item -> openFragment(BottomNavigationPages.PROFILE)
                else -> false
            }
        }
    }

    override fun back() {
        fragmentInteractor?.back()
    }

    override fun openTopic(topicTitle: String, streamTitle: String, streamId: Int) {
        fragmentInteractor?.openTopic(topicTitle, streamTitle, streamId)
    }

    override fun openStream(streamTitle: String, streamId: Int) {
        fragmentInteractor?.openStream(streamTitle, streamId)
    }

    override fun popStream() {
        fragmentInteractor?.popStream()
    }

    private fun openFragment(page: BottomNavigationPages): Boolean {

        childFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.main_fragment_container,
                childFragmentManager.findFragmentByTag(page.tag) ?: newFragmentInstance(page),
                page.tag
            )
        }
        return true
    }

    private fun newFragmentInstance(page: BottomNavigationPages): Fragment = when (page) {
        BottomNavigationPages.CHANNELS -> ChannelsFragment.newInstance()
        BottomNavigationPages.PEOPLE -> PeopleFragment.newInstance()
        BottomNavigationPages.PROFILE -> ProfileFragment.newInstance()
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
