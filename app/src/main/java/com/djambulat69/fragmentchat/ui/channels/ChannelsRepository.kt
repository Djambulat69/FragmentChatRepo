package com.djambulat69.fragmentchat.ui.channels

import com.djambulat69.fragmentchat.model.network.Subscription
import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class ChannelsRepository @Inject constructor(private val zulipServiceHelper: ZulipServiceHelper) {

    fun subscribeOnStream(subscription: Subscription, inviteOnly: Boolean): Completable =
        zulipServiceHelper.subscribeOnStream(subscription, inviteOnly)

}
