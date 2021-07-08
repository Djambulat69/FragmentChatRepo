package com.djambulat69.fragmentchat.ui

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ActivityMainBinding
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
}
