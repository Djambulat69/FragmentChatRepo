package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ActivityMainBinding
import com.djambulat69.fragmentchat.ui.channels.ChannelsFragment
import com.djambulat69.fragmentchat.ui.people.PeopleFragment
import com.djambulat69.fragmentchat.ui.profile.ProfileFragment
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : MvpAppCompatActivity(), MainActivityView, FragmentInteractor {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    @Inject
    lateinit var presenterProvider: Provider<MainActivityPresenter>

    private val presenter by moxyPresenter { presenterProvider.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as FragmentChatApplication).daggerAppComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.main_fragment_container, ChannelsFragment.newInstance(), BottomNavigationPages.CHANNELS.name)
            }
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.channels_menu_item -> openFragment(BottomNavigationPages.CHANNELS)
                R.id.people_menu_item -> openFragment(BottomNavigationPages.PEOPLE)
                R.id.profile_menu_item -> openFragment(BottomNavigationPages.PROFILE)
                else -> false
            }
        }
    }

    override fun onBackPressed() {
        finish()
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

    private fun openFragment(page: BottomNavigationPages): Boolean {

        supportFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.main_fragment_container,
                supportFragmentManager.findFragmentByTag(page.tag) ?: newFragmentInstance(page),
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
}
