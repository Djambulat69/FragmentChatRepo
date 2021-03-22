package com.djambulat69.fragmentchat.ui.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.djambulat69.fragmentchat.databinding.FragmentPeopleBinding
import com.djambulat69.fragmentchat.model.User
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import com.djambulat69.fragmentchat.ui.people.recyclerview.UsersAdapter
import com.djambulat69.fragmentchat.ui.people.recyclerview.UsersHolderFactory
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class PeopleFragment : MvpAppCompatFragment(), PeopleView {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    private val presenter: PeoplePresenter by moxyPresenter { PeoplePresenter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usersRecyclerView.adapter = UsersAdapter(UsersHolderFactory())
    }

    override fun showUsers(users: List<User>) {
        (binding.usersRecyclerView.adapter as UsersAdapter).items = users.map { user ->
            UserUI(user)
        }
    }

    companion object {
        fun newInstance() = PeopleFragment()
    }
}
