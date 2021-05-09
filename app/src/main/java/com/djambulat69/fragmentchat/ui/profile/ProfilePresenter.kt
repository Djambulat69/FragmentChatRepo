package com.djambulat69.fragmentchat.ui.profile

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import javax.inject.Inject

private const val TAG = "ProfilePresenter"

class ProfilePresenter @Inject constructor(private val repository: ProfileRepository) : MvpPresenter<ProfileView>() {

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
            repository.getProfile()
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
