package com.djambulat69.fragmentchat.ui.channels

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChannelsBinding
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.SearchQueryListener
import com.djambulat69.fragmentchat.utils.getCurrentFragments
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

private const val SEARCH_DEBOUNCE_MILLIS = 400L

class ChannelsFragment : MvpAppCompatFragment(), ChannelsView, CreateStreamDialogFragment.CreateStreamListener {

    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var presenterProvider: Provider<ChannelsPresenter>

    private val presenter: ChannelsPresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        FragmentChatApplication.INSTANCE.daggerAppComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.channelsViewPager.apply {
            adapter = ChannelsViewPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
            offscreenPageLimit = ChannelsPages.values().size - 1
        }
        TabLayoutMediator(binding.channelsTabLayout, binding.channelsViewPager) { tab, position ->
            tab.text = getString(
                when (position) {
                    ChannelsPages.SUBSCRIBED.ordinal -> R.string.subscribed_streams_tab_title
                    ChannelsPages.ALL_STREAMS.ordinal -> R.string.all_streams_tab_title
                    else -> throw IllegalStateException("Undefined tab position: $position")
                }
            )
        }.attach()

        binding.createStreamButton.setOnClickListener {
            CreateStreamDialogFragment.newInstance().show(childFragmentManager, null)
        }
        presenter.susbcribeOnSearching(getSearchBarObservable())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        presenter.unsubscribeFromViews()
    }

    override fun makeSearch(query: String) {
        binding.channelsViewPager.getCurrentFragments(childFragmentManager).forEach {
            if (it is SearchQueryListener) {
                it.makeSearch(query)
            }
        }
    }

    override fun createStream(name: String, description: String, inviteOnly: Boolean) {
        Toast.makeText(requireContext(), "Stream created", Toast.LENGTH_SHORT).show()
    }

    private fun getSearchBarObservable(): Observable<String> = Observable.create { emitter ->
        (binding.channelsToolbar.menu.findItem(R.id.search_menu_item).actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    emitter.onNext(newText.orEmpty().trim())
                    return true
                }

            }
        )
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}
