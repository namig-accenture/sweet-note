package app.views.homeactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Observer
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

internal class HomePresenter(private val homeActivity: HomeActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable
    private lateinit var roomDisposable: CompositeDisposable

    private val viewModel by lazy { homeActivity.homeViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposables() {
        disposables = CompositeDisposable()
        roomDisposable = CompositeDisposable()
        viewModel.fetchUserNotes { roomDisposable += it }
                .observe(homeActivity, Observer(homeActivity::updateNoteList))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initObservables() {
        disposables += viewModel.loadState.subscribeBy(
                onNext = homeActivity::handleLoadState,
                onError = { it.log<HomePresenter>("While observing load state") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        roomDisposable.clear()
    }

    fun onAddButtonClicked(view: View) {
        homeActivity.openAddEditDialog(view)
    }
}