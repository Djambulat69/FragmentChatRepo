package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.network.ZulipServiceHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import javax.inject.Inject

private const val TAG = "ProfilePresenter"

class ProfilePresenter @Inject constructor(private val zulipService: ZulipServiceHelper) : MvpPresenter<ProfileView>() {

    private val compositeDisposable = CompositeDisposable()


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun showProfile() {
        compositeDisposable.add(
            zulipService.getOwnUser()
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
