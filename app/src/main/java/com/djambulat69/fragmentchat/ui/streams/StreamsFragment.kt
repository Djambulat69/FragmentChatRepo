package com.djambulat69.fragmentchat.ui.streams

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.databinding.FragmentStreamsBinding
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.ui.FragmentInteractor
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamsAdapter
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamsHolderFactory
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

private const val ARG_TAB_POSITION = "tab_position"

class StreamsFragment : MvpAppCompatFragment(), StreamsView {

    private var fragmentInteractor: FragmentInteractor? = null
    private var tabPosition: Int? = null
    private val presenter: StreamsPresenter by moxyPresenter { StreamsPresenter() }

    private var _binding: FragmentStreamsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is FragmentInteractor) {
            fragmentInteractor = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabPosition = it.getInt(ARG_TAB_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStreamsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.streamsRecyclerView.adapter = StreamsAdapter(StreamsHolderFactory())
    }

    companion object {
        @JvmStatic
        fun newInstance(tabPosition: Int) =
            StreamsFragment().apply {
                arguments = Bundle().apply {
                    putInt(
                        ARG_TAB_POSITION, tabPosition
                    )
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showStreams(streamUIs: List<ViewTyped>) {
        (binding.streamsRecyclerView.adapter as StreamsAdapter).items = streamUIs
    }

    override fun toggleStreamItem(isChecked: Boolean, topicUIs: List<TopicUI>, position: Int) {
        presenter.streamUIs = presenter.streamUIs.toMutableList().apply {
            if (isChecked) {
                addAll(position + 1, topicUIs)
            } else {
                removeAll(topicUIs)
            }
        }
        presenter.showStreams()
    }

    override fun openTopicFragment(topic: Topic, streamTitle: String) {
        fragmentInteractor?.openTopic(topic, streamTitle)
    }
}
