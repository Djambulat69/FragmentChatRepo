package com.djambulat69.fragmentchat.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChannelsBinding
import com.djambulat69.fragmentchat.ui.SearchQueryListener
import com.djambulat69.fragmentchat.utils.getCurrentFragments

import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ChannelsFragment : MvpAppCompatFragment(), ChannelsView {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    private val presenter: ChannelsPresenter by moxyPresenter { ChannelsPresenter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.channelsViewPager.adapter =
            ChannelsViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        TabLayoutMediator(binding.channelsTabLayout, binding.channelsViewPager) { tab, position ->
            tab.text = getString(
                when (position) {
                    0 -> R.string.subscribed_streams_tab_title
                    1 -> R.string.all_streams_tab_title
                    else -> throw IllegalStateException("Undefined tab position: $position")
                }
            )
        }.attach()
        presenter.observeSearchText(getSearchBarObservable())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.dispose()
    }

    override fun makeSearch(query: String) {
        binding.channelsViewPager.getCurrentFragments(childFragmentManager).forEach {
            if (it is SearchQueryListener) {
                it.makeSearch(query)
            }
        }
    }

    private fun getSearchBarObservable(): Observable<String> = Observable.create { emitter ->
        (binding.channelsToolbar.menu.findItem(R.id.search_menu_item).actionView as SearchView).setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                emitter.onNext(newText.orEmpty().trim())
                return true
            }

        })
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}
