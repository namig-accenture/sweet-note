package app.views.pinactivity

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.OnLifecycleEvent
import android.view.View
import app.ext.BasePresenter
import app.ext.log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

@Suppress("UNUSED_PARAMETER")
internal class PinPresenter(private val pinActivity: PinActivity) : BasePresenter() {
    private lateinit var disposables: CompositeDisposable

    val viewModel by lazy { pinActivity.pinViewModel }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createDisposable() {
        disposables = CompositeDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun initObservers() {
        disposables += viewModel.observePinChanges(
                onNext = {
                    if (it) {
                        disposables += viewModel.validatePin().subscribeBy(
                                onSuccess = pinActivity::handleValidatingPin,
                                onError = pinActivity::handleValidatingPinError
                        )
                    }
                },
                onError = { it.log<PinPresenter>("While observing pin changes.") }
        )
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun clearDisposables() {
        disposables.clear()
    }

    fun onPinValueChanged(pin: CharSequence?, start: Int, before: Int, count: Int) {
        pin?.let { viewModel.pinObserver.onNext(it) }
    }

    fun onBtnContinueClicked(view: View) {
        disposables += viewModel.registerPin()
                .subscribeBy(
                        onComplete = pinActivity::handleRegisteringPin,
                        onError = pinActivity::handleRegisteringPinError
                )
    }
}