package com.djambulat69.fragmentchat.ui.chat.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.databinding.MessageOptionsBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MessageOptionsBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: MessageOptionsBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = MessageOptionsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        fun newInstance() = MessageOptionsBottomSheetDialog()
    }

}
