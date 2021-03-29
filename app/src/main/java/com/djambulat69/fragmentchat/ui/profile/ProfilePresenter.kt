package com.djambulat69.fragmentchat.ui.profile

import com.djambulat69.fragmentchat.model.db.DataBase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "ProfilePresenter"

class ProfilePresenter : MvpPresenter<ProfileView>() {
    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        showProfile()
    }

    fun showProfile() {
        compositeDisposable.add(
            DataBase.profileSingle
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .delay(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { profile -> viewState.showProfile(profile) },
                    { viewState.showError() }
                )
        )
    }
}
