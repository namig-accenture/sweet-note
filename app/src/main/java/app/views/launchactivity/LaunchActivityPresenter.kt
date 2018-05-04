package app.views.launchactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import app.ext.BasePresenter
import com.jakewharton.rxbinding2.widget.RxRadioGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

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
        disposables += observeCurrentUser()
        disposables += observeSwitchChanges()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    private fun observeSwitchChanges(): Disposable {
        return RxRadioGroup.checkedChanges(dataBinding.viewSwitcher)
                .subscribeBy(
                        onNext = activity::handleSwitchChanges,
                        onError = Timber::e
                )
    }

    private fun observeCurrentUser(): Disposable {
        return viewModel.currentUser
                .doOnSuccess{viewModel.showProgress.set(it.isPresent)}
                .subscribeBy(
                        onSuccess = activity::handleCurrentUserAvailibility,
                        onError = activity::handleCurrentUserAvailibilityError
                )
    }
}