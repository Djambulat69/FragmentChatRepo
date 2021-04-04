package com.djambulat69.fragmentchat.ui.people

import android.util.Log
import com.djambulat69.fragmentchat.model.network.ZulipRemote
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

private const val TAG = "PeoplePresenter"

class PeoplePresenter : MvpPresenter<PeopleView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun getUsers() {
        compositeDisposable.add(
            ZulipRemote.getUsers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .delay(2, TimeUnit.SECONDS)
                .map { allUsersResponse -> allUsersResponse.users.map { user -> UserUI(user) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { userUIs -> viewState.showUsers(userUIs) },
                    { exception ->
                        viewState.showError()
                        Log.e(TAG, exception.stackTraceToString())
                    }
                )
        )
    }
}
