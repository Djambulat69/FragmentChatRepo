package com.djambulat69.fragmentchat.ui.channels

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.DialogCreateStreamBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.TextWatcherAdapter

class CreateStreamDialogFragment : DialogFragment() {

    private var listener: CreateStreamListener? = null

    private var _binding: DialogCreateStreamBinding? = null
    private val binding: DialogCreateStreamBinding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        listener = requireParentFragment() as CreateStreamListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogCreateStreamBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireParentFragment().requireContext())
            .setTitle(R.string.create_stream_text)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                createStream()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int -> dialog.cancel() }
            .create()
    }

    override fun onStart() {
        super.onStart()

        getPositiveButton()?.isEnabled = binding.nameEditText.text.isNotBlank()
        setupTextWatchers()
    }

    private fun createStream() {
        listener?.createStream(
            binding.nameEditText.text.toString().trim(),
            binding.descriptionEditText.text.toString(),
            binding.inviteOnlySwitch.isChecked
        )
    }

    private fun setupTextWatchers() {
        binding.nameEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                getPositiveButton()?.isEnabled = text.isNotBlank()
            }
        })
    }

    private fun getPositiveButton(): Button? = (requireDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

    interface CreateStreamListener {
        fun createStream(name: String, description: String, inviteOnly: Boolean)
    }

    companion object {
        fun newInstance() = CreateStreamDialogFragment()
    }

}
