package com.djambulat69.fragmentchat.ui.people

import com.djambulat69.fragmentchat.model.db.DataBase
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import java.util.concurrent.TimeUnit

class PeoplePresenter : MvpPresenter<PeopleView>() {

    private val compositeDisposable = CompositeDisposable()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getUsers()
    }

    fun showUsers(userUIs: List<UserUI>) {
        viewState.showUsers(userUIs)
    }

    fun dispose() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private fun getUsers() {
        compositeDisposable.add(
            DataBase.usersSingle
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .map { users -> users.map { user -> UserUI(user) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { userUIs -> showUsers(userUIs) }
        )
    }
}
