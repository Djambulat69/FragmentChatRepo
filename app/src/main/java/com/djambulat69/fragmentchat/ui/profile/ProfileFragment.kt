package com.djambulat69.fragmentchat.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentProfileBinding
import com.djambulat69.fragmentchat.model.Profile
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var _errorBinding: ErrorLayoutBinding? = null
    private val errorBinding get() = _errorBinding!!

    private val presenter: ProfilePresenter by moxyPresenter { ProfilePresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        _errorBinding = ErrorLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUIVisibility(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _errorBinding = null
        _binding = null
    }

    override fun showProfile(profile: Profile) {
        with(binding) {
            profileName.text = profile.name
            profileStatus.text = profile.status
        }
        setLoading(false)
        setUIVisibility(true)
    }

    override fun showError() {
        setUIVisibility(false)
        setLoading(false)
        errorBinding.retryButton.isVisible = true
        errorBinding.checkConnectionTextView.isVisible = true
    }

    override fun showLoading() {
        setLoading(true)
    }

    private fun setUIVisibility(isVisible: Boolean) {
        with(binding) {
            profileStatus.isVisible = isVisible
            profileName.isVisible = isVisible
            profileOnlineStatus.isVisible = isVisible
            logoutButton.isVisible = isVisible
            profileAvatar.isVisible = isVisible
        }
    }

    private fun setLoading(isLoadingVisible: Boolean) {
        binding.shimmerProfile.isVisible = isLoadingVisible
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
