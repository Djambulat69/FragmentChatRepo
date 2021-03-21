package com.djambulat69.fragmentchat.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChannelsBinding
import com.google.android.material.tabs.TabLayoutMediator

class ChannelsFragment : Fragment() {

    private lateinit var binding: FragmentChannelsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.channelsViewPager.adapter =
            ChannelsViewPagerAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(binding.channelsTabLayout, binding.channelsViewPager) { tab, position ->
            tab.text = getString(
                when (position) {
                    0 -> R.string.subscribed_streams_tab_title
                    1 -> R.string.all_streams_tab_title
                    else -> throw IllegalStateException("Undefined tab position: $position")
                }
            )
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ChannelsFragment()
    }
}
