package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.IdRes
import android.widget.RadioGroup
import app.ext.BasePresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class LaunchActivityPresenter(private val activity: LaunchActivity) : BasePresenter() {

    private lateinit var disposables: CompositeDisposable

    val dataBinding by lazy { activity.dataBinding }
    val viewModel by lazy { activity.launchActivityViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        onSwitchChanged(dataBinding.viewSwitcher, viewModel.currentStateId.get())
        disposables += observeCurrentUser()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onSwitchChanged(radioGroup: RadioGroup, @IdRes checkedId: Int) {
        viewModel.currentStateId.set(checkedId)
        activity.handleSwitchChanges(checkedId)
    }

    private fun observeCurrentUser(): Disposable {
        return viewModel.currentUser
                .doOnSuccess { viewModel.showProgress.set(it.isPresent) }
                .subscribeBy(
                        onSuccess = activity::handleCurrentUserAvailibility,
                        onError = activity::handleCurrentUserAvailibilityError
                )
    }
}