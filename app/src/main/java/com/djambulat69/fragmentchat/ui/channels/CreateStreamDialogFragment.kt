package com.djambulat69.fragmentchat.ui.channels

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.djambulat69.fragmentchat.R
import com.djambulat69.fragmentchat.databinding.DialogCreateStreamBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.internal.TextWatcherAdapter

class CreateStreamDialogFragment : DialogFragment() {

    private var _binding: DialogCreateStreamBinding? = null
    private val binding: DialogCreateStreamBinding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogCreateStreamBinding.inflate(layoutInflater)

        return MaterialAlertDialogBuilder(requireParentFragment().requireContext())
            .setTitle(R.string.create_stream_text)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                setFragmentResult(
                    CREATE_STREAM_REQUEST_KEY,
                    bundleOf(
                        NAME_RESULT_KEY to binding.nameEditText.text.toString(),
                        DESCRIPTION_RESULT_KEY to binding.descriptionEditText.text.toString(),
                        INVITE_ONLY_RESULT_KEY to binding.inviteOnlySwitch.isChecked
                    )
                )
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

    private fun setupTextWatchers() {
        binding.nameEditText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                getPositiveButton()?.isEnabled = text.isNotBlank()
            }
        })
    }

    private fun getPositiveButton(): Button? = (requireDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)

    companion object {
        fun newInstance() = CreateStreamDialogFragment()

        const val CREATE_STREAM_REQUEST_KEY = "create_stream_request"

        const val NAME_RESULT_KEY = "name_result"
        const val DESCRIPTION_RESULT_KEY = "description_result"
        const val INVITE_ONLY_RESULT_KEY = "invite_only_result"
    }

}
