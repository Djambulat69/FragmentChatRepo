package com.djambulat69.fragmentchat.ui.channels

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.FragmentChannelsBinding
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.utils.getCurrentFragments
import com.djambulat69.fragmentchat.utils.setChildFragmentResultListener
import com.djambulat69.fragmentchat.utils.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import io.reactivex.rxjava3.core.Observable
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class ChannelsFragment : MvpAppCompatFragment(), ChannelsView {

    private val binding by viewBinding { FragmentChannelsBinding.inflate(layoutInflater) }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setCreateStreamFragmentResultListener()

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
        presenter.unsubscribeFromViews()
    }

    override fun makeSearch(query: String) {
        binding.channelsViewPager.getCurrentFragments(childFragmentManager).forEach {
            if (it is SearchQueryListener) {
                it.makeSearch(query)
            }
        }
    }

    override fun showStreamCreatedSnackbar() {
        Snackbar.make(binding.root, R.string.stream_created, Snackbar.LENGTH_SHORT).show()
    }

    override fun showError() {
        Snackbar.make(binding.root, R.string.error_text, Snackbar.LENGTH_SHORT).show()
    }

    override fun updateStreams() {
        binding.channelsViewPager.getCurrentFragments(childFragmentManager).forEach {
            if (it is StreamCreateListener) {
                it.updateStreams()
            }
        }
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

    private fun setCreateStreamFragmentResultListener() {
        setChildFragmentResultListener(CreateStreamDialogFragment.CREATE_STREAM_REQUEST_KEY) { _: String, bundle: Bundle ->

            val streamName = bundle.getString(CreateStreamDialogFragment.NAME_RESULT_KEY) as String
            val streamDescription = bundle.getString(CreateStreamDialogFragment.DESCRIPTION_RESULT_KEY) as String
            val streamInviteOnly = bundle.getBoolean(CreateStreamDialogFragment.INVITE_ONLY_RESULT_KEY)

            presenter.createStream(streamName, streamDescription, streamInviteOnly)
        }
    }

    companion object {
        fun newInstance() = ChannelsFragment()
    }
}
