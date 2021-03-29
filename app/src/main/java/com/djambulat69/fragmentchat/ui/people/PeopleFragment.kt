package com.djambulat69.fragmentchat.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.djambulat69.fragmentchat.databinding.ErrorLayoutBinding
import com.djambulat69.fragmentchat.databinding.FragmentPeopleBinding
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import com.djambulat69.fragmentchat.ui.people.recyclerview.UsersAdapter
import com.djambulat69.fragmentchat.ui.people.recyclerview.UsersHolderFactory
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class PeopleFragment : MvpAppCompatFragment(), PeopleView {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!
    private var _errorBinding: ErrorLayoutBinding? = null
    private val errorBinding get() = _errorBinding!!

    private val presenter: PeoplePresenter by moxyPresenter { PeoplePresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        _errorBinding = ErrorLayoutBinding.bind(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usersRecyclerView.adapter = UsersAdapter(UsersHolderFactory())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _errorBinding = null
        _binding = null
    }

    override fun onDestroy() {
        presenter.dispose()
        super.onDestroy()
    }

    override fun showUsers(userUIs: List<UserUI>) {
        setUiVisibility(true)
        setLoading(false)
        (binding.usersRecyclerView.adapter as UsersAdapter).items = userUIs
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
        binding.shimmerUserList.isVisible = isLoadingVisible
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}
