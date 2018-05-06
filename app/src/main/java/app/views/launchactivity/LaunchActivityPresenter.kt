package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.IdRes
import android.widget.RadioGroup
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.BehaviorSubject

@Suppress("UNUSED_PARAMETER")
internal class LaunchActivityPresenter(private val activity: LaunchActivity) : BasePresenter() {

    private lateinit var disposables: CompositeDisposable
    private lateinit var switchObserver: BehaviorSubject<Int>

    val viewModel by lazy { activity.launchActivityViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        switchObserver = BehaviorSubject.createDefault(viewModel.currentStateId.get())
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initDisposables() {
        disposables += observeCurrentUser()
        disposables += observeSwitchChanges()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onSwitchChanged(radioGroup: RadioGroup, @IdRes checkedId: Int) {
        switchObserver.onNext(checkedId)
    }

    private fun observeSwitchChanges(): Disposable {
        return switchObserver
                .doOnNext(viewModel.currentStateId::set)
                .subscribeBy(
                        onNext = activity::handleSwitchChanges,
                        onError = { it.log<LaunchActivityPresenter>("While observing switch changing.") }
                )
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