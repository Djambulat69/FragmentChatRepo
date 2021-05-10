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
    }

    override fun setError(visible: Boolean) {
        setUIVisibility(!visible)
        errorBinding.retryButton.isVisible = visible
        errorBinding.checkConnectionTextView.isVisible = visible
    }

    override fun setLoading(visible: Boolean) {
        setUIVisibility(!visible)
        binding.includeShimmerProfile.shimmerProfile.isVisible = visible
    }

    private fun setUIVisibility(isVisible: Boolean) {
        with(binding) {
            profileName.isVisible = isVisible
            profileOnlineStatus.isVisible = isVisible
            profileAvatar.isVisible = isVisible
        }
    }

    companion object {
        fun newInstance() = ProfileFragment()
    }
}
