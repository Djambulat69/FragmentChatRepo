package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.network.User
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val zulipService: ZulipServiceHelper) {

    fun getProfile(): Single<User> = zulipService.getOwnUser()

}
