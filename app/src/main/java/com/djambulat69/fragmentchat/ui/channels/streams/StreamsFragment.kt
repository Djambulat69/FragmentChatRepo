package com.djambulat69.fragmentchat.ui.channels.streams

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentStreamsBinding
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.SearchQueryListener
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamsAdapter
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamsHolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

private const val ARG_TAB_POSITION = "tab_position"

class StreamsFragment : MvpAppCompatFragment(), StreamsView, SearchQueryListener {

    private var fragmentInteractor: FragmentInteractor? = null

    private val presenter: StreamsPresenter by moxyPresenter { StreamsPresenter(requireArguments().getInt(ARG_TAB_POSITION)) }

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = _binding!!
    private var _errorBinding: ErrorLayoutBinding? = null
    private val errorBinding get() = _errorBinding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        _errorBinding = ErrorLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.streamsRecyclerView.adapter = StreamsAdapter(StreamsHolderFactory())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _errorBinding = null
    }

    override fun showStreams(streamUIs: List<ViewTyped>) {
        (binding.streamsRecyclerView.adapter as StreamsAdapter).items = streamUIs
        setLoading(false)
        setUiVisibility(true)
    }

    override fun showError() {
        setLoading(false)
        setUiVisibility(false)
        errorBinding.checkConnectionTextView.isVisible = true
        errorBinding.retryButton.isVisible = true
    }

    override fun showLoading() {
        hideError()
        setUiVisibility(false)
        setLoading(true)
    }

    override fun openTopicFragment(topic: Topic, streamTitle: String) {
        fragmentInteractor?.openTopic(topic, streamTitle)
    }

    override fun makeSearch(query: String) {
        presenter.searchStreams(query)
    }

    private fun setUiVisibility(isVisible: Boolean) {
        binding.streamsRecyclerView.isVisible = isVisible
    }

    private fun setLoading(isLoadingVisible: Boolean) {
        binding.shimmerStreamList.isVisible = isLoadingVisible
    }

    private fun hideError() {
        errorBinding.checkConnectionTextView.isVisible = false
        errorBinding.retryButton.isVisible = false
    }

    companion object {
        fun newInstance(tabPosition: Int) =
            StreamsFragment().apply {
                arguments = bundleOf(ARG_TAB_POSITION to tabPosition)
            }
    }
}
