package com.djambulat69.fragmentchat.ui.people

import com.djambulat69.fragmentchat.model.network.User
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class PeopleRepository @Inject constructor(private val zulipService: ZulipServiceHelper) {

    fun getUsers(): Single<List<UserUI>> =
        zulipService.getUsers()
            .flattenAsObservable { allUsersResponse -> allUsersResponse.users.filter { !it.isBot } }
            .flatMapSingle { user ->
                zipUserWithUserPresence(user).subscribeOn(Schedulers.io()).retry()
            }
            .toList()

    private fun zipUserWithUserPresence(user: User): Single<UserUI> {
        return zulipService.getUserPresence(user.email).zipWith(Single.just(user)) { userPresenceResponse, _ ->
            UserUI(user, userPresenceResponse.presence.aggregated)
        }
    }
}
