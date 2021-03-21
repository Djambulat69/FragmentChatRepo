package com.djambulat69.fragmentchat.ui.streams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamsAdapter
import com.djambulat69.fragmentchat.ui.streams.recyclerview.StreamsHolderFactory
import com.djambulat69.fragmentchat.ui.streams.recyclerview.TopicUI
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

private const val ARG_TAB_POSITION = "tab_position"

class StreamsFragment : MvpAppCompatFragment(), StreamsView {

    private var tabPosition: Int? = null
    private lateinit var streamsRecyclerView: RecyclerView
    private val presenter: StreamsPresenter by moxyPresenter { StreamsPresenter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tabPosition = it.getInt(ARG_TAB_POSITION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_streams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        streamsRecyclerView = view.findViewById(R.id.streams_recycler_view)
        streamsRecyclerView.adapter = StreamsAdapter(StreamsHolderFactory())
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

    override fun showStreams(streamUIs: List<ViewTyped>) {
        (streamsRecyclerView.adapter as StreamsAdapter).items = streamUIs
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
}
