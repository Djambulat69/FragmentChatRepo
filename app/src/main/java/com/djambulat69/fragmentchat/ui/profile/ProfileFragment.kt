package com.djambulat69.fragmentchat.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentProfileBinding
import com.djambulat69.fragmentchat.model.network.User
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment : MvpAppCompatFragment(), ProfileView {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var _errorBinding: ErrorLayoutBinding? = null
    private val errorBinding get() = _errorBinding!!

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    private val presenter: ProfilePresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as FragmentChatApplication).daggerAppComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        _errorBinding = ErrorLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _errorBinding = null
        _binding = null
    }

    override fun showProfile(user: User) {
        with(binding) {
            Glide.with(this@ProfileFragment).load(user.avatarUrl).into(profileAvatar)
            profileName.text = user.fullName
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
        setUIVisibility(false)
        setLoading(true)
    }

    private fun setUIVisibility(isVisible: Boolean) {
        with(binding) {
            profileName.isVisible = isVisible
            profileOnlineStatus.isVisible = isVisible
            profileAvatar.isVisible = isVisible
        }
    }

    private fun setLoading(isLoadingVisible: Boolean) {
        binding.includeShimmerProfile.shimmerProfile.isVisible = isLoadingVisible
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
