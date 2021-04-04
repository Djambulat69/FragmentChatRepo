package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.network.ZulipRemote
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter

private const val TAG = "ProfilePresenter"

class ProfilePresenter : MvpPresenter<ProfileView>() {

    private val compositeDisposable = CompositeDisposable()
    private val zulipRemote = ZulipRemote

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showProfile()
    }

    private fun showProfile() {
        compositeDisposable.add(
            zulipRemote.getOwnUser()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { user -> viewState.showProfile(user) },
                    { viewState.showError() }
                )
        )
    }
}
