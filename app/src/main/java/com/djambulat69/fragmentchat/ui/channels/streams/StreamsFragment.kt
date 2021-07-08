package com.djambulat69.fragmentchat.ui.channels.streams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentStreamsBinding
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.channels.SearchQueryListener
import com.djambulat69.fragmentchat.ui.channels.StreamCreateListener
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamDiffCallback
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamsClickMapper
import com.djambulat69.fragmentchat.ui.channels.streams.recyclerview.StreamsHolderFactory
import com.djambulat69.fragmentchat.ui.chat.ChatActivity
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import com.djambulat69.fragmentchat.utils.viewBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

private const val ARG_TAB_POSITION = "tab_position"

class StreamsFragment : MvpAppCompatFragment(), StreamsView, SearchQueryListener, StreamCreateListener {

    private var fragmentInteractor: FragmentInteractor? = null

    @Inject
    lateinit var presenterProvider: Provider<StreamsPresenter>

    private val presenter: StreamsPresenter by moxyPresenter { presenterProvider.get() }

    private val binding by viewBinding { FragmentStreamsBinding.inflate(layoutInflater) }
    private val errorBinding by viewBinding { ErrorLayoutBinding.bind(binding.root) }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }

        (context.applicationContext as FragmentChatApplication).daggerAppComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.streamsRecyclerView.adapter = AsyncAdapter(StreamsHolderFactory(), StreamDiffCallback, StreamsClickMapper())

        presenter.tabPosition = requireArguments().getInt(ARG_TAB_POSITION)
        presenter.subscribeOnClicks(
            (binding.streamsRecyclerView.adapter as AsyncAdapter<*>)
                .getClicks()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unsubscribeFromViews()
    }

    override fun showStreams(streamUIs: List<ViewTyped>) {
        (binding.streamsRecyclerView.adapter as AsyncAdapter<ViewTyped>).items = streamUIs
    }

    override fun setError(visible: Boolean) {
        errorBinding.checkConnectionTextView.isVisible = visible
        errorBinding.retryButton.isVisible = visible
        setUiVisibility(!visible)
    }

    override fun showToastError() {
        Toast.makeText(requireContext(), getString(R.string.error_text), Toast.LENGTH_SHORT).show()
    }

    override fun setLoading(visible: Boolean) {
        binding.includeShimmerFragmentStreams.shimmerStreamList.isVisible = visible
        setUiVisibility(!visible)
    }

    override fun openTopicFragment(topicTitle: String, streamTitle: String, streamId: Int) {
        val topicChatIntent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.ARG_STREAM_TITLE, streamTitle)
            putExtra(ChatActivity.ARG_STREAM_ID, streamId)
            putExtra(ChatActivity.ARG_TOPIC, topicTitle)
        }

        requireContext().startActivity(topicChatIntent)
    }

    override fun openStreamFragment(streamTitle: String, streamId: Int) {
        val streamChatIntent = Intent(requireContext(), ChatActivity::class.java).apply {
            putExtra(ChatActivity.ARG_STREAM_TITLE, streamTitle)
            putExtra(ChatActivity.ARG_STREAM_ID, streamId)
        }

        requireContext().startActivity(streamChatIntent)
    }

    override fun makeSearch(query: String) {
        presenter.searchStreams(query)
    }

    override fun updateStreams() {
        presenter.updateStreams()
    }

    private fun setUiVisibility(isVisible: Boolean) {
        binding.streamsRecyclerView.isVisible = isVisible
    }

    companion object {
        fun newInstance(tabPosition: Int) =
            StreamsFragment().apply {
                arguments = bundleOf(ARG_TAB_POSITION to tabPosition)
            }
    }
}
