package com.djambulat69.fragmentchat.ui.people

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentPeopleBinding
import com.djambulat69.fragmentchat.ui.FragmentChatApplication
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserDiffCallback
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import com.djambulat69.fragmentchat.ui.people.recyclerview.UsersHolderFactory
import com.djambulat69.fragmentchat.utils.recyclerView.AsyncAdapter
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class PeopleFragment : MvpAppCompatFragment(), PeopleView {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!
    private var _errorBinding: ErrorLayoutBinding? = null
    private val errorBinding get() = _errorBinding!!

    @Inject
    lateinit var presenterProvider: Provider<PeoplePresenter>

    private val presenter: PeoplePresenter by moxyPresenter { presenterProvider.get() }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (context.applicationContext as FragmentChatApplication).daggerAppComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        _errorBinding = ErrorLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usersRecyclerView.adapter = AsyncAdapter(UsersHolderFactory(Glide.with(this)), UserDiffCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _errorBinding = null
        _binding = null
    }

    override fun showUsers(userUIs: List<UserUI>) {
        setUiVisibility(true)
        setLoading(false)
        (binding.usersRecyclerView.adapter as AsyncAdapter<UserUI>).items = userUIs
    }

    override fun showError() {
        setUiVisibility(false)
        setLoading(false)
        errorBinding.checkConnectionTextView.isVisible = true
        errorBinding.retryButton.isVisible = true
    }

    override fun showLoading() {
        setLoading(true)
    }

    private fun setUiVisibility(isVisible: Boolean) {
        binding.usersRecyclerView.isVisible = isVisible
    }

    private fun setLoading(isLoadingVisible: Boolean) {
        binding.includeShimmerFragmentPeople.shimmerUserList.isVisible = isLoadingVisible
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}
