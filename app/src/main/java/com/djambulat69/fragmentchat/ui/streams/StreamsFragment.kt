package com.djambulat69.fragmentchat.ui.streams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.model.Stream
import com.djambulat69.fragmentchat.model.Topic
import com.djambulat69.fragmentchat.utils.recyclerView.ViewTyped

private const val ARG_TAB_POSITION = "tab_position"

class StreamsFragment : Fragment() {
    private var tabPosition: Int? = null

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

        val streamsRecyclerView = view.findViewById<RecyclerView>(R.id.streams_recycler_view)
        var streams = listOf<ViewTyped>()
        streams = listOf(
            Stream("general", listOf(Topic("Testing", 332))),
            Stream("memes", listOf(Topic("Hello", 23)))
        ).flatMap {
            listOf(StreamUI(it) { isChecked, topicUIs, position ->
                streams = if (isChecked) {
                    streams.toMutableList().apply { addAll(position + 1, topicUIs) }
                } else {
                    streams.toMutableList().apply { removeAll(topicUIs) }
                }
                (streamsRecyclerView.adapter as StreamsAdapter).submitList(streams)
            })
        }
        streamsRecyclerView.adapter = StreamsAdapter()
        (streamsRecyclerView.adapter as StreamsAdapter).submitList(streams)
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
}
