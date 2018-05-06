package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.IdRes
import android.widget.RadioGroup
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

@Suppress("UNUSED_PARAMETER")
internal class LaunchActivityPresenter(private val activity: LaunchActivity) : BasePresenter() {

    private lateinit var disposables: CompositeDisposable

    val viewModel by lazy { activity.launchActivityViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += viewModel.observeCurrentUser(
                onSuccess = activity::handleCurrentUserAvailibility,
                onError = activity::handleCurrentUserAvailibilityError
        )
        disposables += viewModel.observeSwitchChanges(
                onNext = activity::handleSwitchChanges,
                onError = { it.log<LaunchActivityPresenter>("While observing switch changing.") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onSwitchChanged(radioGroup: RadioGroup, @IdRes checkedId: Int) {
        viewModel.switchObserver.onNext(checkedId)
    }
}