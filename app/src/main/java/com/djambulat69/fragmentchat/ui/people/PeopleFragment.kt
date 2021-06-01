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
import com.djambulat69.fragmentchat.utils.viewBinding
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class PeopleFragment : MvpAppCompatFragment(), PeopleView {

    private val binding by viewBinding { FragmentPeopleBinding.inflate(layoutInflater) }
    private val errorBinding by viewBinding { ErrorLayoutBinding.bind(binding.root) }

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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usersRecyclerView.adapter = AsyncAdapter(UsersHolderFactory(Glide.with(this)), UserDiffCallback)
    }

    override fun showUsers(userUIs: List<UserUI>) {
        (binding.usersRecyclerView.adapter as AsyncAdapter<UserUI>).items = userUIs
    }

    override fun setError(visible: Boolean) {
        errorBinding.checkConnectionTextView.isVisible = visible
        errorBinding.retryButton.isVisible = visible
        setUiVisibility(!visible)
    }

    override fun setLoading(visible: Boolean) {
        binding.includeShimmerFragmentPeople.shimmerUserList.isVisible = visible
        setUiVisibility(!visible)
    }

    private fun setUiVisibility(isVisible: Boolean) {
        binding.usersRecyclerView.isVisible = isVisible
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}
