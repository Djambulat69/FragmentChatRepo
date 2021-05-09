package com.djambulat69.fragmentchat.ui.people

import android.util.Log
import com.djambulat69.fragmentchat.ui.people.recyclerview.UserUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.MvpPresenter
import javax.inject.Inject

private const val TAG = "PeoplePresenter"

class PeoplePresenter @Inject constructor(private val repository: PeopleRepository) : MvpPresenter<PeopleView>() {

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
            repository.getUsers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { viewState.showLoading() }
                .observeOn(Schedulers.computation())
                .map { userUIs: List<UserUI> -> userUIs.sortedBy { it.user.fullName } }
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
