package com.djambulat69.fragmentchat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.databinding.FragmentProfileBinding
import com.djambulat69.fragmentchat.model.Profile
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val presenter: ProfilePresenter by moxyPresenter { ProfilePresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun showProfile(profile: Profile) {
        with(binding) {
            profileName.text = profile.name
            profileStatus.text = profile.status
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
